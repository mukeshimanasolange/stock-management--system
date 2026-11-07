import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { FiBox, FiLock, FiMail } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';
import { getErrorMessage } from '../api/client';

export default function LoginPage() {
  const { user, login } = useAuth();
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  if (user) return <Navigate to="/" replace />;

  async function submit(event) {
    event.preventDefault(); setBusy(true); setError('');
    try { await login(credentials); navigate('/', { replace: true }); }
    catch (err) { setError(getErrorMessage(err)); }
    finally { setBusy(false); }
  }
  return (
    <main className="login-page">
      <div className="login-decoration"><div className="login-orbit orbit-one" /><div className="login-orbit orbit-two" /><div className="login-brand"><span className="brand-mark"><FiBox /></span> stockwise</div><div className="login-message"><span className="eyebrow">INVENTORY, IN CONTROL</span><h1>Everything in<br />its right place.</h1><p>One clear view of your products, stock flow, and team.</p></div><div className="login-decoration-footer">STOCK MANAGEMENT PLATFORM · 2026</div></div>
      <section className="login-panel"><form onSubmit={submit} className="login-form">
        <div className="mobile-login-brand"><FiBox /> stockwise</div>
        <span className="eyebrow">WELCOME BACK</span><h2>Sign in to your account</h2><p className="muted">Enter your credentials to continue.</p>
        {error && <div className="alert alert-danger">{error}</div>}
        <label>Email address<div className="input-icon"><FiMail /><input type="email" required autoComplete="username" value={credentials.email} onChange={(e) => setCredentials({ ...credentials, email: e.target.value })} placeholder="you@company.com" /></div></label>
        <label>Password<div className="input-icon"><FiLock /><input type="password" required autoComplete="current-password" value={credentials.password} onChange={(e) => setCredentials({ ...credentials, password: e.target.value })} placeholder="Enter your password" /></div></label>
        <button className="btn btn-primary w-100 login-submit" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
        <div className="login-footer">New to Stockwise? <Link to="/register">Create a staff account</Link></div>
      </form></section>
    </main>
  );
}
