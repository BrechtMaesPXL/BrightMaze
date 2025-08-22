import { setActivePinia, createPinia } from 'pinia';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import axios from 'axios';
import { useMapStore } from '../../stores/map';

// Mock axios
vi.mock('axios');

describe('Map Store', () => {
  let mapStore;

  beforeEach(() => {
    setActivePinia(createPinia());
    mapStore = useMapStore();
  });

  it('should initialize with default state', () => {
    expect(mapStore.locations).toEqual([]);
    expect(mapStore.z).toEqual([]);
    expect(mapStore.selectedDate).toBe(1992);
    expect(mapStore.error).toBeNull();
  });

  it('should return a formatted date', () => {
    const formattedDate = mapStore.formattedDate;
    const expectedDate = new Intl.DateTimeFormat('en-GB').format(new Date(mapStore.selectedDate, 0, 1));
    expect(formattedDate).toBe(expectedDate);
  });

  it('should populate locations and z arrays with getAllLocation', async () => {
    axios.get.mockResolvedValueOnce({
      data: {
        CA: 141,
        TX: 123,
        NY: 76
      }
    });

    await mapStore.getAllLocation();

    expect(mapStore.locations).toEqual(['CA', 'TX', 'NY']);
    expect(mapStore.z).toEqual([141, 123, 76]);
    expect(mapStore.error).toBeNull();
  });

  it('should populate default values on getAllLocation error', async () => {
    axios.get.mockRejectedValueOnce(new Error('Network Error'));

    await mapStore.getAllLocation();

    expect(mapStore.locations.length).toBe(50);
    expect(mapStore.z.every(val => val === 0)).toBe(true);
    expect(mapStore.error).toContain('Could not fetch wildfire data');
  });

  it('should update z values with getLocationsFiresByTime', async () => {
    axios.get.mockResolvedValueOnce({
      data: {
        wildfires_by_state: {
          CA: 200,
          TX: 150,
          NY: 90
        }
      }
    });

    await mapStore.getLocationsFiresByTime();

    expect(mapStore.locations.length).toBe(50);
    expect(mapStore.z.length).toBe(50);
    expect(mapStore.z[mapStore.locations.indexOf('CA')]).toBe(200);
    expect(mapStore.z[mapStore.locations.indexOf('TX')]).toBe(150);
    expect(mapStore.z[mapStore.locations.indexOf('NY')]).toBe(90);
    expect(mapStore.error).toBeNull();
  });

  it('should log selectedDate and wildfire data in getLocationsFiresByTime', async () => {
    const consoleSpy = vi.spyOn(console, 'log');
    const mockWildfireData = {
      CA: 100,
      TX: 80
    };

    axios.get.mockResolvedValueOnce({
      data: { wildfires_by_state: mockWildfireData }
    });

    await mapStore.getLocationsFiresByTime();

    expect(consoleSpy).toHaveBeenCalledWith("Selected year:", mapStore.selectedDate);
    expect(consoleSpy).toHaveBeenCalledWith("Locations:", expect.any(Array));
    expect(consoleSpy).toHaveBeenCalledWith("Wildfire counts (z):", expect.any(Array));

    consoleSpy.mockRestore();
  });
});
