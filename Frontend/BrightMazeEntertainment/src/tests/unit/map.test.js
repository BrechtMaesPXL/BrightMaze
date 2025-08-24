import { setActivePinia, createPinia } from 'pinia';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import axios from 'axios';
import { useMapStore } from './../../stores/map';

// Axios mocken
vi.mock('axios');

describe('map store', () => {
  let mapStore;

  beforeEach(() => {
    setActivePinia(createPinia());
    mapStore = useMapStore();
  });

  it('should initialize with an empty locations array', () => {
    expect(mapStore.locations).toEqual([]);
    expect(mapStore.z).toEqual([]);
    expect(mapStore.selectedDate).toBe(1992);
    expect(mapStore.error).toBeNull();
  });

  it('should fetch and store all locations', async () => {
    axios.get.mockResolvedValueOnce({
      data: { CA: 100, TX: 50, NY: 20 }
    });

    await mapStore.getAllLocation();

    expect(mapStore.locations).toEqual(['CA', 'TX', 'NY']);
    expect(mapStore.z).toEqual([100, 50, 20]);
    expect(mapStore.error).toBeNull();
  });

  it('should handle error on getAllLocation', async () => {
    axios.get.mockRejectedValueOnce(new Error('Network Error'));

    await mapStore.getAllLocation();

    expect(mapStore.locations.length).toBe(50); // fallback state
    expect(mapStore.z.every(val => val === 0)).toBe(true);
    expect(mapStore.error).toContain('Could not fetch wildfire data');
  });

  it('should fetch locations fires by time and modify z values', async () => {
    axios.get.mockResolvedValueOnce({
      data: {
        wildfires_by_state: {
          CA: 250,
          TX: 180,
          NY: 150,
        }
      }
    });

    await mapStore.getLocationsFiresByTime();

    expect(mapStore.locations.length).toBe(50);
    expect(mapStore.z.length).toBe(50);
    expect(mapStore.z[mapStore.locations.indexOf('CA')]).toBe(250);
    expect(mapStore.z[mapStore.locations.indexOf('TX')]).toBe(180);
    expect(mapStore.z[mapStore.locations.indexOf('NY')]).toBe(150);
    expect(mapStore.error).toBeNull();
  });

  it('should handle error on getLocationsFiresByTime', async () => {
    axios.get.mockRejectedValueOnce(new Error('Server down'));

    await mapStore.getLocationsFiresByTime();

    expect(mapStore.locations.length).toBe(50); // fallback
    expect(mapStore.z.every(z => z === 0)).toBe(true);
    expect(mapStore.error).toContain('Could not fetch wildfires by year');
  });

  it('should correctly return a formatted date', () => {
    const formattedDate = mapStore.formattedDate;
    const expected = new Intl.DateTimeFormat('en-GB').format(new Date(1992, 0, 1));

    expect(formattedDate).toBe(expected);
    expect(typeof formattedDate).toBe('string');
  });
});
