import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(localStorage.getItem('stockwise.token')));

  const logout = useCallback(() => {
    localStorage.removeItem('stockwise.token');
    setUser(null);
  }, []);

  useEffect(() => {
    if (!localStorage.getItem('stockwise.token')) return;
    api.get('/auth/me')
      .then(({ data }) => setUser(data))
      .catch(logout)
      .finally(() => setLoading(false));
  }, [logout]);

  const login = async (credentials) => {
    const { data } = await api.post('/auth/login', credentials);
    localStorage.setItem('stockwise.token', data.token);
    setUser(data.user);
  };

  const register = async (profile) => {
    const { data } = await api.post('/auth/register', profile);
    localStorage.setItem('stockwise.token', data.token);
    setUser(data.user);
  };

  const value = useMemo(() => ({ user, loading, login, register, logout }), [user, loading, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
