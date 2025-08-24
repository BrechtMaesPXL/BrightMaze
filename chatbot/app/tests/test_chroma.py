import pytest
from util import timestamp_to_datetime_str
import chromaDB
from chromaDB import (
    add_event,
    update_event,
    delete_event,
    get_events,
    get_event_By_Id,
    get_event_by_query,
    get_event_Date_specific,
)


class FakeCollection:
    """
    A simple in-memory stub for ChromaDB collection supporting add, upsert, get, delete, and query.
    """
    def __init__(self):
        # storage maps id -> metadata dict (with numeric dates)
        self.storage = {}

    def add(self, documents, embeddings, metadatas, ids):
        eid = ids[0]
        self.storage[eid] = metadatas[0]

    def upsert(self, ids, documents, metadatas, embeddings):
        eid = ids[0]
        self.storage[eid] = metadatas[0]

    def get(self, ids=None, **kwargs):
        if ids is None:
            # return all entries
            return {"ids": list(self.storage.keys()), "metadatas": [meta for meta in self.storage.values()]}
        eid = ids[0]
        if eid not in self.storage:
            return {"ids": [], "metadatas": []}
        return {"ids": [eid], "metadatas": [self.storage[eid]]}

    def delete(self, ids):
        eid = ids[0]
        if eid not in self.storage:
            raise KeyError(f"Event with ID '{eid}' does not exist.")
        del self.storage[eid]

    def query(self, query_embeddings, n_results):
        # ignore embeddings, return up to n_results existing
        ids = list(self.storage.keys())[:n_results]
        metas = [self.storage[e] for e in ids]
        return {"ids": [ids], "metadatas": [metas]}


@pytest.fixture(autouse=True)
def stub_embedding(monkeypatch):
    # stub create_embedding to return a fixed vector
    fake_emb = [0.1, 0.2, 0.3]
    monkeypatch.setattr(chromaDB, "create_embedding", lambda doc, client, model=None: fake_emb)
    return fake_emb


@pytest.fixture
def dummy_openai():
    # openai_client is unused by our stub
    return object()


@pytest.fixture
def collection():
    return FakeCollection()


def test_add_event_returns_id_and_stores_metadata(collection, dummy_openai, stub_embedding):
    doc = "doc text"
    meta = {"a": 1}
    eid = "id1"
    returned = add_event(doc, meta, eid, collection, dummy_openai)
    assert returned == eid
    assert eid in collection.storage
    assert collection.storage[eid] == meta


def test_update_event_overwrites_metadata(collection, dummy_openai, stub_embedding):
    # initial add via add_event
    add_event("d", {"x":0}, "id2", collection, dummy_openai)
    # update
    new_meta = {"x": 42}
    ret = update_event("id2", "doc2", new_meta, collection, dummy_openai)
    assert ret == "id2"
    assert collection.storage["id2"] == new_meta


def test_delete_event_removes_or_errors(collection):
    # add then delete
    add_event("d", {"k":9}, "id3", collection, dummy_openai)
    delete_event("id3", collection)
    assert "id3" not in collection.storage
    # deleting again raises
    with pytest.raises(KeyError):
        delete_event("id3", collection)


def test_get_events_returns_all(collection, dummy_openai, stub_embedding):
    # add two events
    add_event("d1", {"m":1}, "e1", collection, dummy_openai)
    add_event("d2", {"m":2}, "e2", collection, dummy_openai)
    ids, metas = get_events(collection)
    assert set(ids) == {"e1","e2"}
    assert any(meta.get("m") == 1 for meta in metas)
    assert any(meta.get("m") == 2 for meta in metas)


def test_get_event_by_id_returns_or_errors(collection, dummy_openai, stub_embedding):
    add_event("d", {"p":5}, "ix", collection, dummy_openai)
    result = get_event_By_Id("ix", collection)
    assert result["ids"] == ["ix"]
    assert result["metadatas"][0]["p"] == 5
    with pytest.raises(KeyError):
        get_event_By_Id("nope", collection)


def test_get_event_by_query_returns_matching(collection, dummy_openai, stub_embedding):
    add_event("foo", {"q":1}, "q1", collection, dummy_openai)
    # query always returns up to n_results
    out = get_event_by_query("foo query", collection, dummy_openai, n_results=1)
    assert "ids" in out and isinstance(out["ids"], list)
    assert out["ids"][0][0] == "q1"


def test_get_event_date_specific_converts_dates(collection):
    # timestamps for 2021-01-01 and 2021-01-02
    start_ts = 1609459200
    end_ts   = 1609545600
    # add event
    collection.storage["dte"] = {"startDate": start_ts, "endDate": end_ts}
    events = get_event_Date_specific(start_ts - 10, end_ts + 10, collection)
    assert isinstance(events, list) and len(events) == 1
    ev = events[0]
    assert ev["id"] == "dte"
    # converted back to ISO strings
    assert ev["startDate"].startswith("2021-01-01T00:00:00")
    assert ev["endDate"].startswith("2021-01-02T00:00:00")
