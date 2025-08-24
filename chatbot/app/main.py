import json
import os
from fastapi import FastAPI, Request
from dotenv import load_dotenv
import openai
from fastapi import FastAPI, HTTPException, FastAPI
from pydantic import BaseModel
from typing import Any, List
from fastapi.middleware.cors import CORSMiddleware
import chromaDB
from datetime import date
from pathlib import Path
import chromadb
from settings import settings
from util import *
from fastapi.concurrency import run_in_threadpool


class EventCreate(BaseModel):
    eventName: str
    startDate: str
    endDate: str
    location: str
    eventDescription: str


class EventResponse(EventCreate):
    id: str


# Model voor locatie verzoek
class LocationRequest(BaseModel):
    current_building: str


# ChatRequest model
class ChatRequest(BaseModel):
    message: str
    history: List[dict]


class RouteSettingsRequest(BaseModel):
    enabled: bool


today = date.isoformat(date.today())

# Definieer de basisdirectory
BASE_DIR = Path(__file__).resolve().parent
CURRENT_BUILDING_PATH = BASE_DIR / "current_building.json"
ROUTE_SETTINGS_PATH = BASE_DIR / "route_settings.json"

INTENT_FUNCTION = {
    "name": "classify_query",
    "description": (
        "Classify the user’s question into one of these intents:\n"
        "- events_list: user wants to see a list of (e.g. “Give me a list of all events”)\n"
        "- event_detail: user is asking about a specific event or event theme or events in a place (e.g. “When does Corda Campus Hackathon start?” or “Are there events over games?” or “Are there events happening in corda arena?” )\n"
        "- events_date_specific: user wants events in a specific date or range (e.g. “What events are happening in the summer?” or “Show me events between July 1 and July 10.” or “Which events are happening this year”)\n"
        "- events_time_specific: user wants all events happening at a specific time or time range (e.g. “Give me all events that are happening at 12:00” or “Give me all events that are happening in the afternoon”)"
        "- route: user needs directions between buildings on the Corda Campus (not general campus location)\n"
        "- general_info: conversational or informational queries not covered by events or route (e.g. “What’s the dress code?” or “Good morning”)"
    ),
    "parameters": {
        "type": "object",
        "properties": {
            "intent": {
                "type": "string",
                "enum": [
                    "event_list",
                    "event_detail",
                    "events_date_specific",
                    "route",
                    "general_info",
                ],
                "description": "One of the five classified intents.",
            },
            "start_date": {
                "type": "string",
                "format": "date-time",
                "description": f"Starting date and time for events_date_specific queries in ISO format (YYYY-MM-DD HH:MM:SS). Reference date: {today}.",
            },
            "end_date": {
                "type": "string",
                "format": "date",
                "description": f"Ending date and time for events_date_specific queries in ISO format (YYYY-MM-DD HH:MM:SS). Reference date: {today}.",
            },
            "start_hour": {
                "type": "integer",
                "minimum": 0,
                "maximum": 23,
                "description": "For events_time_specific intent: Starting hour (0-23) for time-based queries (e.g., 12 for 'events at noon'). Night start at 0",
            },
            "end_hour": {
                "type": "integer",
                "minimum": 0,
                "maximum": 23,
                "description": "For events_time_specific intent: The ending hour (0-23) for time range queries (e.g. 20 'events between 12 and 20'). It must not be used for specific time queries with only starting time (e.g., 'events at 12:00'). Night ends at 5",
            },
            "destination": {
                "type": "string",
                "description": "When intent is route, the destination building.",
            },
            "output_message": {
                "type": "string",
                "description": "Response message to show to the user based on the intent. For example if route: here is a route to {destination}. If events_time_specific: Here are all the events that start in {start_hour} etc.",
            },
        },
        "required": ["intent"],
    },
}

# Laad de omgevingsvariabelen
load_dotenv(Path(__file__).parent / ".env")

openai_client = openai.OpenAI(api_key=os.getenv("AZURE_OPENAI_API_KEY"))
SPRING_BOOT_BASE_URL = (
    os.getenv("SPRING_BOOT_BASE_URL", "http://localhost:8095") + "/post/api/events"
)


async def call_openai(messages, functions=None):
    return await run_in_threadpool(
        openai_client.chat.completions.create,
        model="gpt-4o",
        messages=messages,
        functions=functions,
    )


async def lifespan(app: FastAPI):
    global chroma_client, collection
    chroma_client = chromadb.HttpClient(
        host=settings.chroma_host, port=settings.chroma_port
    )
    collection = chroma_client.get_or_create_collection("events")
    yield  # Lifespan context ends here


app = FastAPI(lifespan=lifespan)

# Configureer CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=['http://gateway-service:8095'],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Endpoint voor chat
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.post("/chat")
async def chat_completion(request: ChatRequest):
    try:
        logger.info(f"Received chat request: {request.message}")
        history = request.history
        for msg in history:
            msg["role"] = msg["role"].lower()
        user_message = request.message
        translated_message = translate_to_english(user_message, openai_client)
        logger.info(f"Translated message: {translated_message}")

        messages = [
            {
                "role": "system",
                "content": (
                    "You are the Corda Campus assistant. "
                    "Users may ask in Dutch or English. "
                    "You *must* use one of the provided functions (event_list, event_detail, route, events_date_specific, general_info). "
                    "For event_list, always include an output_message like 'Here are all the events.' "
                    "Otherwise fall back to general_info. "
                    "Do not output anything but the function call or the answer."
                ),
            },
            {"role": "user", "content": translated_message},
        ]

        intent_resp = await call_openai(messages, functions=[INTENT_FUNCTION])
        logger.info(f"OpenAI response: {intent_resp}")

        intent_call = intent_resp.choices[0].message.function_call
        args = json.loads(intent_call.arguments)
        logger.info(f"Parsed args: {args}")

        intent = args["intent"]
        logger.info(f"Detected intent: {intent}")

        if intent == "event_list":
            ids, metas = chromaDB.get_events(collection)
            logger.info(f"Retrieved events: {ids}")
            for meta in metas:
                meta = convert_metadata_dates_from_timestamp(meta)
            events = [
                EventResponse(id=evt_id, **meta) for evt_id, meta in zip(ids, metas)
            ]

            output_message = args.get("output_message", "Hier zijn alle evenementen:")
            logger.info(f"Output message: {output_message}")
            translated_message = translate_to_user_language(
                output_message, user_message, openai_client
            )

            history.append(
                {"role": "assistant", "content": translated_message, "type": intent}
            )

            return {
                "response": translated_message,
                "type": intent,
                "history": history,
                "events": events,
            }
        elif intent == "route":
          
            # Check if route functionality is enabled
            try:
                with ROUTE_SETTINGS_PATH.open("r") as f:
                    route_config = json.load(f)
                    print(route_config.get("enabled"))
                    if not route_config.get(
                        "enabled", True
                    ):  # Default to enabled if file doesn't exist
                        response = "I apologize, but the route functionality is currently disabled."
                        translated_response = translate_to_user_language(
                            response, user_message, openai_client
                        )
                        history.append(
                            {
                                "role": "assistant",
                                "content": translated_response,
                                "type": "general_info",
                            }
                        )
                        return {"response": translated_response, "history": history}
            except FileNotFoundError:
                # If file doesn't exist, create it with enabled=True
                with ROUTE_SETTINGS_PATH.open("w") as f:
                    json.dump({"enabled": True}, f)

            with CURRENT_BUILDING_PATH.open("r") as f:
                location_data = json.load(f)
            start = location_data["current_building"]
            end = args["destination"].lower()

            message = validate_route(start, end)

            print(f"start: {start}")
            print(f"einde: {end}")
            if (
                message
            ):  # Als er een melding is (bijvoorbeeld 'Je bevindt je al in dit gebouw.')
                route_response = message
                history.append(
                    {
                        "role": "assistant",
                        "content": route_response,
                        "type": "generla_info",
                    }
                )
                return {"response": route_response, "history": history}

            message = f"Here is a route from {start} to {end}."
            translated_message = translate_to_user_language(
                message, user_message, openai_client
            )
            route_response = translated_message

            history.append(
                {"role": "assistant", "content": route_response, "type": intent}
            )
            return {
                "response": route_response,
                "type": intent,
                "history": history,
                "start": start,
                "end": end,
            }
        elif intent == "event_detail":
            events = chromaDB.get_event_by_query(
                user_message, collection, openai_client, n_results=3
            )
            if not events["ids"]:
                raise HTTPException(
                    status_code=404, detail="Geen evenementen gevonden."
                )
            ids = events["ids"][0]
            metas = events["metadatas"][0]
            for meta in metas:
                meta = convert_metadata_dates_from_timestamp(meta)
            events = [
                EventResponse(id=evt_id, **meta) for evt_id, meta in zip(ids, metas)
            ]

            events_json = json.dumps([e.dict() for e in events])

            system_message = {
                "role": "system",
                "content": f"Answer the user's question based on the events provided. Give only an overview. Use the user's language. Today's date is {today}.",
            }

            messages.clear()
            messages.append(system_message)
            messages.append({"role": "system", "content": events_json})
            messages.append({"role": "user", "content": user_message})
            response = await call_openai(messages)
            response_message = response.choices[0].message.content

            history.append(
                {"role": "assistant", "content": response_message, "type": intent}
            )
            return {"response": response_message, "type": intent, "history": history}
        elif intent == "events_date_specific":
            startdate, enddate = (
                args["start_date"],
                args["end_date"],
            )
            startdate = datetime_str_to_timestamp(startdate)
            enddate = datetime_str_to_timestamp(enddate)
            events = chromaDB.get_event_Date_specific(startdate, enddate, collection)
            if len(events) == 0:
                response_message = "There are no events planned in this period."
                translated_message = translate_to_user_language(
                    response_message, user_message, openai_client
                )
                history.append({"role": "assistant", "content": translated_message})
                return {
                    "response": translated_message,
                    "type": intent,
                    "history": history,
                }

            events_json = json.dumps([e for e in events])

            system_message = {
                "role": "system",
                "content": f"Answer the user's question based on the events provided. Use the user's query language. Today's date is {today}.",
            }
            messages.clear()
            messages.append(system_message)
            messages.append({"role": "system", "content": events_json})
            messages.append({"role": "user", "content": user_message})
            response = await call_openai(messages)
            response_message = response.choices[0].message.content
            history.append(
                {"role": "assistant", "content": response_message, "type": intent}
            )
            return {"response": response_message, "type": intent, "history": history}
        elif intent == "events_time_specific":
            start_time = args["start_hour"]
            try:
                end_time = args["end_hour"]
            except:
                end_time = None
            events = chromaDB.get_events_time_specific(start_time, end_time, collection)

            if not events:
                response_message = "No events found at this time."
                translated_message = translate_to_user_language(
                    response_message, user_message, openai_client
                )
                history.append(
                    {"role": "assistant", "content": translated_message, "type": intent}
                )
                return {
                    "response": translated_message,
                    "type": intent,
                    "history": history,
                }

            # Format events directly
            output_message = args.get("output_message", "Here are the events:")
            translated_message = translate_to_user_language(
                output_message, user_message, openai_client
            )

            history.append(
                {"role": "assistant", "content": translated_message, "type": intent}
            )
            return {
                "response": translated_message,
                "type": intent,
                "history": history,
                "events": events,
            }
        else:
            base_system = (
                "You should only answer questions about the Corda Campus: buildings, events, route navigation, and general information. "
                "You may engage in casual conversation such as “Hello,” “Good morning,” or “How are you,” etc. Respond in the same language as the question. "
                "Always look at the context of the conversation through checking old messages."
                "Provide your answers in plain text, without any Markdown formatting."
            )

            system_message = {"role": "system", "content": base_system}
            messages = [system_message, *history]
            response = await call_openai(messages)
            response_message = response.choices[0].message.content
            history.append(
                {"role": "assistant", "content": response_message, "type": intent}
            )
            return {"response": response_message, "type": intent, "history": history}

    except Exception as e:
        logger.error(f"Error in /chat endpoint: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Serverfout: {str(e)}")


# --------------------------


# Endpoint voor huidige locatie (POST en GET)
@app.post("/current-location")
async def save_location(request_body: LocationRequest, request: Request):
    try:
        building = request_body.current_building.lower().strip()
        if not is_valid_building(building):
            raise HTTPException(
                status_code=400,
                detail=f"Ongeldig gebouw: {building}. Kies uit: {', '.join(ALLOWED_BUILDINGS)}",
            )

        # Controleer of het gebouw exact overeenkomt met een toegestaan gebouw
        if building not in [b.lower() for b in ALLOWED_BUILDINGS]:
            raise HTTPException(
                status_code=400,
                detail=f"Gebouw '{building}' komt niet exact overeen met een toegestaan gebouw.",
            )

        # Maak de locatie data
        location_data = {"current_building": building}

        # Sla op in current_building.json
        with CURRENT_BUILDING_PATH.open("w") as f:
            json.dump(location_data, f, indent=2)

        return {"message": f"Locatie succesvol opgeslagen: {building}"}

    except IOError as e:
        raise HTTPException(
            status_code=500, detail=f"Fout bij het opslaan van de locatie: {str(e)}"
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Serverfout: {str(e)}")


@app.get("/current-location")
async def get_location(request: Request):
    try:
        # Controleer of het bestand bestaat
        if not CURRENT_BUILDING_PATH.exists():
            raise HTTPException(status_code=404, detail="Geen locatie opgeslagen.")

        # Lees het bestand
        with CURRENT_BUILDING_PATH.open("r") as f:
            location_data = json.load(f)

        # Controleer of current_building aanwezig is en geldig
        if "current_building" not in location_data:
            raise HTTPException(
                status_code=400, detail="Ongeldig formaat in current_building.json."
            )

        building = location_data["current_building"]
        if not is_valid_building(building):
            raise HTTPException(
                status_code=400, detail=f"Ongeldig gebouw in bestand: {building}."
            )

        return {"current_building": building}

    except json.JSONDecodeError:
        raise HTTPException(
            status_code=400, detail="Ongeldig JSON-formaat in current_building.json."
        )
    except IOError as e:
        raise HTTPException(
            status_code=500, detail=f"Fout bij het lezen van de locatie: {str(e)}"
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Serverfout: {str(e)}")


@app.post("/events", response_model=EventResponse)
async def create_event(event: EventCreate, id: str):

    doc = f"""eventName: {event.eventName}, startDate: {event.startDate}, endDate: {event.endDate}, location: {event.location}, eventDescription: {event.eventDescription}"""
    metadata = {
        "eventName": event.eventName,
        "startDate": datetime_str_to_timestamp(event.startDate),
        "endDate": datetime_str_to_timestamp(event.endDate),
        "location": event.location,
        "eventDescription": event.eventDescription,
    }
    try:
        new_id = await run_in_threadpool(
            chromaDB.add_event, doc, metadata, id, collection, openai_client
        )

        metadata = convert_metadata_dates_from_timestamp(metadata)

        return EventResponse(id=new_id, **metadata)
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Fout bij het aanmaken van het evenement: {str(e)}"
        )


@app.get("/events", response_model=List[EventResponse])
async def get_events():
    try:
        ids, metas = await run_in_threadpool(chromaDB.get_events, collection)

        for meta in metas:
            meta = convert_metadata_dates_from_timestamp(meta)

        events = [EventResponse(id=evt_id, **meta) for evt_id, meta in zip(ids, metas)]
        return events
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Fout bij het ophalen van evenementen: {str(e)}"
        )


@app.get("/events/{event_id}", response_model=EventResponse)
async def get_event(event_id: str):
    try:
        raw = await run_in_threadpool(collection.get, ids=[event_id])

        if not raw["ids"]:
            raise HTTPException(
                status_code=404, detail=f"Evenement met ID {event_id} niet gevonden."
            )

        meta: dict[str, Any] = convert_metadata_dates_from_timestamp(
            raw["metadatas"][0]
        )

        # 4) Return exactly what EventResponse expects
        return EventResponse(id=event_id, **meta)

    except HTTPException:
        # pass through our 404
        raise
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Fout bij het ophalen van het evenement: {e}"
        )


@app.put("/events/{event_id}", response_model=EventResponse)
async def update_event(event_id: str, event: EventCreate):
    try:
        raw = await run_in_threadpool(collection.get, ids=[event_id])

        if not raw["ids"]:
            raise HTTPException(
                status_code=404, detail=f"Evenement met ID {event_id} niet gevonden."
            )

        doc = f"""eventName: {event.eventName}, startDate: {event.startDate}, endDate: {event.endDate}, location: {event.location}, eventDescription: {event.eventDescription}"""
        metadata = {
            "eventName": event.eventName,
            "startDate": event.startDate,
            "endDate": event.endDate,
            "location": event.location,
            "eventDescription": event.eventDescription,
        }
        metadata = convert_metadata_dates_from_timestamp(metadata)

        updated_id = chromaDB.update_event(
            event_id, doc, metadata, collection, openai_client
        )
        return EventResponse(id=updated_id, **metadata)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Fout bij het bijwerken van het evenement: {str(e)}",
        )


@app.delete("/events/{event_id}", status_code=204)
async def delete_event(event_id: str):
    try:
        await run_in_threadpool(chromaDB.delete_event, event_id, collection)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Fout bij het verwijderen van het evenement: {str(e)}",
        )


@app.post("/route-settings")
async def save_route_enabled(request_body: RouteSettingsRequest):
    try:
        # Save to route_enabled.json
        with ROUTE_SETTINGS_PATH.open("w") as f:
            json.dump({"enabled": request_body.enabled}, f, indent=2)
        return {
            "message": f"Route functionality {'enabled' if request_body.enabled else 'disabled'}"
        }
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Error saving route enabled status: {str(e)}"
        )


@app.get("/route-settings")
async def get_route_settings():
    try:
        if not ROUTE_SETTINGS_PATH.exists():
            # If file doesn't exist, create it with enabled=True
            with ROUTE_SETTINGS_PATH.open("w") as f:
                json.dump({"enabled": True}, f)
            return {"enabled": True}

        # Read the file and parse the JSON
        with ROUTE_SETTINGS_PATH.open("r") as f:
            route_config = json.load(f)

        # Ensure "enabled" exists in the JSON and return its value
        if "enabled" in route_config:
            return {"enabled": route_config["enabled"]}
        else:
            # If "enabled" is missing, default to True and update the file
            route_config["enabled"] = True
            with ROUTE_SETTINGS_PATH.open("w") as f:
                json.dump(route_config, f, indent=2)
            return {"enabled": True}
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Error reading route enabled status: {str(e)}"
        )


# region Use me to run locally
if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="127.0.0.1", port=8075, reload=False)
# endregion
