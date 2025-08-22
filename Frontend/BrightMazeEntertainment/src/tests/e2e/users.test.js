import { test, expect } from '@playwright/test';
import jwt from 'jsonwebtoken';

// Configuration
const BASE_URL = 'http://localhost:8000';
const SECRET_KEY = 'BMMH';
const ALGORITHM = 'HS256';
const ACCESS_TOKEN_EXPIRE_MINUTES = 30;

function generateAccessToken(username) {
  const payload = {
    sub: username,
    exp: Math.floor(Date.now() / 1000) + ACCESS_TOKEN_EXPIRE_MINUTES * 60,
  };
  return jwt.sign(payload, SECRET_KEY, { algorithm: ALGORITHM });
}

async function setupUser(request) {
  const uniqueUsername = `user_${Date.now()}`;
  const userData = {
    username: uniqueUsername,
    email: `${uniqueUsername}@example.com`,
    password: 'testpassword123',
  };

  const response = await request.post(`${BASE_URL}/users`, {
    data: userData,
    headers: { 'Content-Type': 'application/json' },
  });
  await expect(response.status()).toBe(201);
  const user = await response.json();

  const token = generateAccessToken(uniqueUsername);
  return { userId: user.id, token, username: uniqueUsername };
}

test('Create a new user', async ({ request }) => {
  const uniqueUsername = `user_${Date.now()}`;
  const userData = {
    username: uniqueUsername,
    email: `${uniqueUsername}@example.com`,
    password: 'testpassword123',
  };

  const response = await request.post(`${BASE_URL}/users`, {
    data: userData,
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
  });

  await expect(response.status()).toBe(201);
  const responseData = await response.json();
  await expect(responseData.username).toBe(uniqueUsername);
  await expect(responseData.email).toBe(userData.email);
  await expect(responseData.id).toBeDefined();
});

test('Create user with duplicate username', async ({ request }) => {
  const uniqueUsername = `user_${Date.now()}_dup`;
  const userData = {
    username: uniqueUsername,
    email: `${uniqueUsername}@example.com`,
    password: 'testpassword123',
  };

  const firstResponse = await request.post(`${BASE_URL}/users`, {
    data: userData,
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
  });
  await expect(firstResponse.status()).toBe(201);

  const secondResponse = await request.post(`${BASE_URL}/users`, {
    data: userData,
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
  });

  await expect(secondResponse.status()).toBe(400);
  const errorData = await secondResponse.json();
  await expect(errorData.detail).toBe('Username already registered');
});

test('Login and get access token', async ({ request }) => {
  const { username } = await setupUser(request);
  const loginData = {
    username,
    password: 'testpassword123',
  };

  const response = await request.post(`${BASE_URL}/token`, {
    form: loginData,
    timeout: 10000,
  });

  await expect(response.status()).toBe(200);
  const responseData = await response.json();
  await expect(responseData.access_token).toBeDefined();
  await expect(responseData.token_type).toBe('bearer');
});

test('Login with invalid credentials', async ({ request }) => {
  const loginData = {
    username: 'nonexistent_user',
    password: 'wrongpassword',
  };

  const response = await request.post(`${BASE_URL}/token`, {
    form: loginData,
    timeout: 10000,
  });

  await expect(response.status()).toBe(401);
  const errorData = await response.json();
  await expect(errorData.detail).toBe('Incorrect username or password');
});

test('Get current user details', async ({ request }) => {
  const { userId, token, username } = await setupUser(request);

  const response = await request.get(`${BASE_URL}/users/me`, {
    headers: { Authorization: `Bearer ${token}` },
    timeout: 10000,
  });

  await expect(response.status()).toBe(200);
  const responseData = await response.json();
  await expect(responseData.username).toBe(username);
  await expect(responseData.id).toBe(userId);
});


test('Get user by ID', async ({ request }) => {
  const { userId, token, username } = await setupUser(request);

  const response = await request.get(`${BASE_URL}/users/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
    timeout: 10000,
  });

  await expect(response.status()).toBe(200);
  const responseData = await response.json();
  await expect(responseData.username).toBe(username);
  await expect(responseData.id).toBe(userId);
});

test('Deprecated login with email', async ({ request }) => {
  const { userId, username } = await setupUser(request);
  const loginData = {
    email: `${username}@example.com`,
    password: 'testpassword123',
  };

  const response = await request.post(`${BASE_URL}/users/login`, {
    data: loginData,
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000,
  });

  await expect(response.status()).toBe(200);
  const responseData = await response.json();
  await expect(responseData.message).toBe('Login successful');
  await expect(responseData.user_id).toBe(userId);
  await expect(responseData.access_token).toBeDefined();
  await expect(responseData.token_type).toBe('bearer');
});
