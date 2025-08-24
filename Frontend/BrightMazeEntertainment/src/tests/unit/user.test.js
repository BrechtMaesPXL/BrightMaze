import { setActivePinia, createPinia } from 'pinia';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { useUserStore } from './../../stores/user';
import axios from 'axios';

vi.mock('axios');

describe('user store', () => {
  let userStore;

  beforeEach(() => {
    setActivePinia(createPinia());
    userStore = useUserStore();
    localStorage.clear();
  });

  it('should initialize with default state values', () => {
    expect(userStore.id).toBe(null);
    expect(userStore.username).toBe('unknown');
    expect(userStore.email).toBe('');
    expect(userStore.token).toBe(null);
    expect(userStore.isAuthenticated).toBe(false);
  });

  it('should login and store token', async () => {
    const mockToken = 'mocked.jwt.token';

    axios.post.mockResolvedValueOnce({
      data: {
        access_token: mockToken,
        token_type: 'bearer'
      }
    });

    await userStore.login({ username: 'tester', password: '1234' });

    expect(userStore.token).toBe(mockToken);
    expect(localStorage.getItem('token')).toBe(mockToken);
    expect(userStore.username).toBe('tester');
    expect(userStore.isAuthenticated).toBe(true);
  });

  it('should logout and clear token', () => {
    userStore.token = 'some-token';
    localStorage.setItem('token', 'some-token');

    userStore.logout();

    expect(userStore.token).toBe(null);
    expect(localStorage.getItem('token')).toBe(null);
  });

  it('should fetch user data when token is valid', async () => {
    const mockToken = 'valid-token';
    userStore.token = mockToken;

    axios.get.mockResolvedValueOnce({
      data: {
        id: 5,
        username: 'fetchedUser',
        email: 'fetched@example.com'
      }
    });

    await userStore.fetchUser(5);

    expect(axios.get).toHaveBeenCalledWith(
      'http://127.0.0.1:8000/users/5',
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Bearer ${mockToken}`
        })
      })
    );

    expect(userStore.id).toBe(5);
    expect(userStore.username).toBe('fetchedUser');
    expect(userStore.email).toBe('fetched@example.com');
  });

  it('should reset state on fetchUser error (e.g., network failure)', async () => {
    userStore.token = 'bad-token';

    axios.get.mockRejectedValueOnce(new Error('Network error'));

    await userStore.fetchUser(1);

    expect(userStore.id).toBe(0);
    expect(userStore.username).toBe('Username');
    expect(userStore.email).toBe('email@email.com');
  });

  it('should clear token on 401 error', async () => {
    userStore.token = 'expired-token';
    localStorage.setItem('token', 'expired-token');

    axios.get.mockRejectedValueOnce({
      response: { status: 401 }
    });

    await userStore.fetchUser(2);

    expect(userStore.token).toBe(null);
    expect(localStorage.getItem('token')).toBe(null);
    expect(userStore.username).toBe('Username');
  });

  it('should not fetch user if not authenticated', async () => {
    const spy = vi.spyOn(axios, 'get');
    await userStore.fetchUser(999);

    expect(userStore.id).toBe(0);
    expect(userStore.username).toBe('Username');
    expect(userStore.email).toBe('email@email.com');
    expect(spy).not.toHaveBeenCalled();
  });
});
