import { createContext, useContext, useState, useEffect } from 'react'
import { setOn401Handler } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('revolve_token'))
  const [username, setUsername] = useState(() => localStorage.getItem('revolve_username'))

  function login(newToken, newUsername) {
    localStorage.setItem('revolve_token', newToken)
    localStorage.setItem('revolve_username', newUsername)
    setToken(newToken)
    setUsername(newUsername)
  }

  function logout() {
    localStorage.removeItem('revolve_token')
    localStorage.removeItem('revolve_username')
    setToken(null)
    setUsername(null)
  }

  useEffect(() => {
    setOn401Handler(() => logout())
  }, [])

  return (
    <AuthContext.Provider value={{ token, username, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
