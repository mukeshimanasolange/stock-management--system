import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ roles }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="screen-loader">Loading your workspace…</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (roles && !user.roles.some((role) => roles.includes(role)))
    return <Navigate to={user.roles.includes('STAFF') ? '/products' : '/'} replace />;
  return <Outlet />;
}
