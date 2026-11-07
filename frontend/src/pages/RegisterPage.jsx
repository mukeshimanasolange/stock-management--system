import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { FiBox } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';
import { getErrorMessage } from '../api/client';

export default function RegisterPage() {
  const { user, register } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState({ fullName: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  if (user) return <Navigate to="/" replace />;
  async function submit(event) {
    event.preventDefault(); setBusy(true); setError('');
    try { await register(profile); navigate('/products', { replace: true }); }
    catch (err) { setError(getErrorMessage(err)); }
    finally { setBusy(false); }
  }
  return <main className="login-page">
    <div className="login-decoration"><div className="login-orbit orbit-one" /><div className="login-orbit orbit-two" /><div className="login-brand"><span className="brand-mark"><FiBox /></span> stockwise</div><div className="login-message"><span className="eyebrow">YOUR INVENTORY, SIMPLIFIED</span><h1>Make room<br />for better flow.</h1><p>Join your team and start tracking stock with confidence.</p></div><div className="login-decoration-footer">STOCK MANAGEMENT PLATFORM · 2026</div></div>
    <section className="login-panel"><form onSubmit={submit} className="login-form">
      <div className="mobile-login-brand"><FiBox /> stockwise</div><span className="eyebrow">CREATE AN ACCOUNT</span><h2>Join your organization</h2><p className="muted">New accounts receive the STAFF role.</p>
      {error && <div className="alert alert-danger">{error}</div>}
      <label>Full name<input className="form-control" required maxLength="120" value={profile.fullName} onChange={(e) => setProfile({ ...profile, fullName: e.target.value })} placeholder="Your name" /></label>
      <label>Email address<input className="form-control" type="email" required maxLength="190" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })} placeholder="you@company.com" /></label>
      <label>Password<input className="form-control" type="password" required minLength="8" maxLength="72" value={profile.password} onChange={(e) => setProfile({ ...profile, password: e.target.value })} placeholder="At least 8 characters" /></label>
      <button className="btn btn-primary w-100 login-submit" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}</button>
      <div className="login-footer">Already have an account? <Link to="/login">Sign in</Link></div>
    </form></section>
  </main>;
}
