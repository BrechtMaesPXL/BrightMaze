from datetime import datetime, timezone


# Lijst van toegestane gebouwen
ALLOWED_BUILDINGS = [
    "corda 1",
    "corda 2",
    "corda 3",
    "corda 4",
    "corda 5",
    "corda 6",
    "corda 7",
    "corda 8",
    "cordaat",
    "corda bar",
    "corda arena",
]


def translate_to_english(message: str, openai_client) -> str:
    """Translate a message to English."""
    system_message = {
        "role": "system",
        "content": (
            "You are a translation assistant specialized in translating to English.\n"
            "Rules:\n"
            "1. Always translate the input to English\n"
            "2. Preserve any markdown formatting in the text\n"
            "3. If the text is already in English, verify it's correct English\n"
            "4. Return only the translated text, no explanations\n"
            "5. Maintain any technical terms as they are\n"
            "6. Keep the same formatting (newlines, spaces) as the input"
        ),
    }

    messages = [
        system_message,
        {"role": "user", "content": f"Translate this to English: {message}"},
    ]

    translation_response = openai_client.chat.completions.create(
        model="gpt-4o",
        messages=messages,
        temperature=0.1,  # Lower temperature for more consistent translations
    )

    return translation_response.choices[0].message.content


def translate_to_user_language(message: str, user_message: str, openai_client) -> str:
    """Vertaal een bericht naar de taal van de gebruiker."""
    system_message = {
        "role": "system",
        "content": (
            "You are a translation assistant. "
            "Your task is to:\n"
            "1. Detect the language of the user's last message\n"
            "2. Translate the given message into that same language\n"
            "3. Always return a translation, even if you think it's the same language\n"
            "4. Maintain any markdown formatting in the translation\n"
            "Do not add any explanations, just return the translated text."
        ),
    }

    messages = [
        system_message,
        {
            "role": "user",
            "content": f"User's message: {user_message}\nTranslate this: {message}",
        },
    ]

    translation_response = openai_client.chat.completions.create(
        model="gpt-4o",
        messages=messages,
        temperature=0.1,  # Lower temperature for more consistent translations
    )

    return translation_response.choices[0].message.content


def datetime_str_to_timestamp(dt_str: str) -> float:
    """Converteer een ISO-datetime string (bijv. '2025-12-12T15:00:00') naar een UNIX-timestamp."""
    dt = datetime.fromisoformat(dt_str)  # parses the T-style string
    if dt.tzinfo is None:
        dt = dt.replace(tzinfo=timezone.utc)  # assume UTC if no offset given
    return dt.timestamp()


def timestamp_to_datetime_str(ts: float) -> str:
    """Converteer een UNIX-timestamp (seconden sinds epoch UTC) naar een ISO-datetime string 'YYYY-MM-DDTHH:MM:SS'."""
    dt = datetime.fromtimestamp(ts, tz=timezone.utc)
    return dt.isoformat(timespec="seconds")


def convert_metadata_dates_from_timestamp(metadata: dict) -> dict:
    """Converteer de UNIX-timestamp velden in metadata (startDate, endDate)
    naar naïeve ISO strings 'YYYY-MM-DDTHH:MM:SS' die geschikt zijn voor Java LocalDateTime.
    """

    for key in ("startDate", "endDate"):
        ts = metadata.get(key)
        if isinstance(ts, (int, float)):
            # Interpret ts as seconds since epoch UTC, then drop tzinfo
            dt = datetime.fromtimestamp(ts, tz=timezone.utc).replace(tzinfo=None)
            metadata[key] = dt.strftime("%Y-%m-%dT%H:%M:%S")
    return metadata


def is_valid_building(building: str) -> bool:
    """Check of het gebouw een geldig gebouw is."""
    return building.lower() in [b.lower() for b in ALLOWED_BUILDINGS]


def validate_route(start: str, end: str):
    """Controleer of de route geldig is."""

    if not is_valid_building(start):
        return "Ongeldige startlocatie"
    if not is_valid_building(end):
        return "Ongeldige eindlocatie"
    if start.lower() == end.lower():
        return "Je bevindt je al in dit gebouw."
    return None  # Geen melding
