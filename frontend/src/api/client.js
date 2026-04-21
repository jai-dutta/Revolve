const BASE_URL = '/api'

function getToken() {
  return localStorage.getItem('revolve_token')
}

let on401Handler = null
export function setOn401Handler(handler) {
  on401Handler = handler
}

async function request(path, options = {}) {
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  let response
  try {
    response = await fetch(`${BASE_URL}${path}`, { ...options, headers })
  } catch {
    throw new Error('Could not reach the server. Is the backend running?')
  }

  if (response.status === 204) return null

  const text = await response.text()
  const body = text ? safeParse(text) : null

  if (response.status === 401 && !path.startsWith('/auth/')) {
    if (on401Handler) on401Handler()
    throw new Error('Your session expired. Please sign in again.')
  }

  if (!response.ok) {
    const message =
      body?.errors?.[0]?.message ||
      body?.message ||
      body?.error ||
      `Request failed (${response.status})`
    throw new Error(message)
  }

  return body
}

function safeParse(text) {
  try { return JSON.parse(text) } catch { return null }
}

export const api = {
  auth: {
    register: (username, password) =>
      request('/auth/register', { method: 'POST', body: JSON.stringify({ username, password }) }),
    login: (username, password) =>
      request('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) }),
  },

  activities: {
    list: () => request('/recurring-activities'),
    create: (dto) =>
      request('/recurring-activities/create', { method: 'POST', body: JSON.stringify(dto) }),
    update: (id, dto) =>
      request(`/recurring-activities/${id}`, { method: 'PUT', body: JSON.stringify(dto) }),
    delete: (id) =>
      request(`/recurring-activities/${id}`, { method: 'DELETE' }),
  },

  instances: {
    getWeek: (weekStart) => {
      const param = weekStart ? `?weekStart=${weekStart}` : ''
      return request(`/activity-instances${param}`)
    },
    toggleComplete: (id) =>
      request(`/activity-instances/${id}/complete`, { method: 'PUT' }),
    backlog: () => request('/activity-instances/backlog'),
  },
}
