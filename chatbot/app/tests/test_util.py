import pytest
import json
from util import *


class DummyChoice:
    def __init__(self, content, func_call=None):
        # mimic the OpenAI shape
        self.message = type("M", (), {"content": content, "function_call": func_call})


class DummyResponse:
    def __init__(self, choices):
        self.choices = choices


class DummyOpenAI:
    def __init__(self):
        # Replaced in each test
        self._next = None

        class Comps:
            pass

        self.chat = type("Chat", (), {})()
        self.chat.completions = type("Completions", (), {})()
        # point create at our stub
        self.chat.completions.create = self._create_stub

    def _create_stub(self, *, model, messages):
        # return whatever was pre-loaded in _next
        return self._next


@pytest.fixture
def dummy_client():
    return DummyOpenAI()


def test_datetime_str_to_timestamp_and_back():
    iso = "2025-12-12T15:00:00"
    ts = datetime_str_to_timestamp(iso)
    # Allow slight float difference
    assert abs(ts - 1765551600.0) < 1
    iso_back = timestamp_to_datetime_str(ts)
    assert iso_back.startswith("2025-12-12T15:00:00")


def test_convert_metadata_dates_from_timestamp():
    meta = {"startDate": 1609459200, "endDate": 1609545600}
    out = convert_metadata_dates_from_timestamp(meta.copy())
    assert out["startDate"] == "2021-01-01T00:00:00"
    assert out["endDate"] == "2021-01-02T00:00:00"


def test_is_valid_building_and_validate_route():
    assert is_valid_building("Corda 1")
    assert not is_valid_building("Unknown")
    assert validate_route("corda 1", "corda 2") is None
    assert validate_route("corda 1", "corda 1") == "Je bevindt je al in dit gebouw."
    assert validate_route("bad", "corda 2") == "Ongeldige startlocatie"


def test_translate_to_english(monkeypatch, dummy_client):
    # arrange: stub OpenAI to return "Hello world"
    dummy_client._next = DummyResponse([DummyChoice("Hello world")])

    # act
    out = translate_to_english("Hallo wereld", dummy_client)

    # assert
    assert out == "Hello world"



def test_translate_to_user_language(monkeypatch, dummy_client):
    # arrange: stub OpenAI to return "Goedemorgen"
    dummy_client._next = DummyResponse([DummyChoice("Goedemorgen")])

    # act
    out = translate_to_user_language(
        message="Good morning", user_message="Good morning", openai_client=dummy_client
    )

    # assert
    assert out == "Goedemorgen"
