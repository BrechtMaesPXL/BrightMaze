from datetime import datetime
from util import (
    datetime_str_to_timestamp,
    timestamp_to_datetime_str,
    convert_metadata_dates_from_timestamp,
)
from typing import List, Dict, Any


def add_event(
    doc: str,
    metadata: dict,
    id,
    collection,
    openai_client,
    model="text-embedding-3-small",
) -> str:
    """
    Voegt een evenement toe aan de ChromaDB collectie.
    Genereert een nieuwe `ID` op basis van de bestaande IDs in de `collectie`.

    """
    # 1) Genereer een embedding voor de document
    embedding = create_embedding(doc, openai_client, model)

    # 2) Voeg de document toe aan de ChromaDB collectie
    collection.add(
        documents=[doc], embeddings=[embedding], metadatas=[metadata], ids=[id]
    )
    return id


def update_event(
    event_id: str,
    doc: str,
    metadata: dict,
    collection,
    openai_client,
    model="text-embedding-3-small",
) -> str:
    """
    Updatet de event met `event_id` in `collection`.
    """

    # 1) Embed de document weer
    # Dit is nodig omdat de embedding kan zijn veranderd
    embedding = create_embedding(doc, openai_client, model)

    # 2) Overschrijf de document
    collection.upsert(
        ids=[event_id], documents=[doc], metadatas=[metadata], embeddings=[embedding]
    )

    return event_id


def delete_event(event_id: str, collection):
    """
    Verwijdert de event met `event_id` uit `collection`.
    Geeft KeyError als de ID niet bestaat.
    """
    # 0) Kijk na of de ID bestaat
    existing = collection.get(ids=[event_id])["ids"]
    if not existing:
        raise KeyError(f"Event with ID '{event_id}' does not exist.")

    # 1) Verwijder de document
    collection.delete(ids=[event_id])


def get_events(collection):

    all_items = collection.get()

    ids = all_items["ids"]
    metadatas = all_items["metadatas"]

    return ids, metadatas


def get_event_By_Id(event_id: str, collection):
    """
    Geeft de event met `event_id` terug.
    Geeft KeyError als de ID niet bestaat.
    """
    # 0) Kijk na of de ID bestaat
    existing = collection.get(ids=[event_id])["ids"]
    if not existing:
        raise KeyError(f"Event with ID '{event_id}' does not exist.")

    # 1) Pak de document
    event = collection.get(ids=[event_id])

    return event


def get_event_by_query(
    query: str, collection, openai_client, model="text-embedding-3-small", n_results=3
) -> dict:
    """
    Geeft de events afhankelijk van `query` terug.
    """
    # 1) Genereer een embedding voor de query
    embedding = create_embedding(query, openai_client, model)

    # 2) Zoek de document
    events = collection.query(
        query_embeddings=[embedding],
        n_results=n_results,
    )

    return events


def get_event_Date_specific(
    startdate,
    enddate,
    collection,
) -> dict:
    """
    Geeft de events afhankelijk van datum terug.
    """

    results = collection.get(
        where={
            "$and": [{"startDate": {"$lte": enddate}}, {"endDate": {"$gte": startdate}}]
        },
        include=["metadatas"],
        limit=1000,
    )

    ids = results["ids"]
    metadatas = results["metadatas"]

    for meta in metadatas:
        # convert date to timestamp
        meta["startDate"] = timestamp_to_datetime_str(meta["startDate"])
        meta["endDate"] = timestamp_to_datetime_str(meta["endDate"])

    # combine into a list of events
    events = [{"id": eid, **meta} for eid, meta in zip(ids, metadatas)]

    return events


def get_events_time_specific(
    start_hour: int,
    end_hour: int,
    collection,
) -> List[Dict[str, Any]]:
    """
    Geeft alle evenementen terug waarvan de startdatum binnen het opgegeven tijdsbereik valt.
    Houdt rekening met zowel reguliere tijdsperioden als perioden die over middernacht gaan.

    Args:
        start_hour: Startuur (0-23)
        end_hour: Einduur (0-23), kan kleiner zijn dan start_hour voor nachtelijke perioden
        collection: ChromaDB collectie om te doorzoeken
    """
    ids, metas = get_events(collection)
    matches = []

    for eid, meta in zip(ids, metas):
        meta = convert_metadata_dates_from_timestamp(meta)
        start_date = meta.get("startDate")
        if start_date is None:
            continue

        dt = datetime.fromisoformat(start_date)
        current_hour = dt.hour

        if end_hour is not None:
            if end_hour < start_hour:  # Night hours (crosses midnight)
                # Match if hour is >= start_hour (e.g., 20:00) OR <= end_hour (e.g., 5:00)
                if current_hour >= start_hour or current_hour <= end_hour:
                    matches.append({"id": eid, **meta})
            else:  # Regular hours
                if start_hour <= current_hour <= end_hour:
                    matches.append({"id": eid, **meta})
        elif current_hour == start_hour:  # Specific hour search
            matches.append({"id": eid, **meta})

    return matches


def create_embedding(request: str, openai_client, model) -> list:
    """
    Genereert een embedding voor de document.
    """
    resp = openai_client.embeddings.create(input=[request], model=model)
    embedding = resp.data[0].embedding

    return embedding
