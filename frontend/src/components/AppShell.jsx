import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { FiBox, FiChevronDown, FiClipboard, FiFileText, FiHome, FiLogOut, FiMenu, FiPackage, FiTruck, FiUsers } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/', label: 'Dashboard', icon: FiHome, roles: ['ADMIN', 'STOREKEEPER'] },
  { to: '/products', label: 'Products', icon: FiPackage, roles: ['ADMIN', 'STOREKEEPER', 'STAFF'] },
  { to: '/suppliers', label: 'Suppliers', icon: FiTruck, roles: ['ADMIN', 'STOREKEEPER'] },
  { to: '/users', label: 'User management', icon: FiUsers, roles: ['ADMIN'] },
  { to: '/stock/incoming', label: 'Receive stock', icon: FiBox, roles: ['ADMIN', 'STOREKEEPER'] },
  { to: '/stock/outgoing', label: 'Issue stock', icon: FiBox, roles: ['ADMIN', 'STOREKEEPER'] },
  { to: '/requests', label: 'Stock requests', icon: FiClipboard, roles: ['ADMIN', 'STOREKEEPER', 'STAFF'] },
  { to: '/reports', label: 'Reports', icon: FiFileText, roles: ['ADMIN', 'STOREKEEPER'] },
];

export default function AppShell() {
  const { user, logout } = useAuth();
  const location = useLocation();
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand"><span className="brand-mark"><FiBox /></span><span>stockwise</span></div>
        <div className="nav-caption">WORKSPACE</div>
        <nav className="side-nav">
          {links.filter((link) => link.roles.some((role) => user.roles.includes(role))).map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} end={to === '/'} className={({ isActive }) => `side-link ${isActive ? 'active' : ''}`}>
              <Icon /><span>{label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="profile-chip"><div className="avatar">{user.fullName?.slice(0, 1).toUpperCase()}</div><div className="profile-copy"><strong>{user.fullName}</strong><span>{user.roles.join(' · ')}</span></div><FiChevronDown /></div>
          <NavLink to="/profile" className={({ isActive }) => `side-link ${isActive ? 'active' : ''}`}><FiUsers /><span>My profile</span></NavLink>
          <button className="side-link logout-link" onClick={logout}><FiLogOut /><span>Sign out</span></button>
        </div>
      </aside>
      <main className="main-panel">
        <header className="topbar">
          <button className="mobile-menu" aria-label="Open menu" onClick={() => document.body.classList.toggle('sidebar-open')}><FiMenu /></button>
          <div className="breadcrumb">Stockwise <span>/</span> {location.pathname === '/' ? 'Overview' : location.pathname.split('/').filter(Boolean).join(' / ')}</div>
          <div className="topbar-user"><span className="status-dot" /> Inventory system <div className="avatar small">{user.fullName?.slice(0, 1).toUpperCase()}</div></div>
        </header>
        <div className="page-content"><Outlet /></div>
      </main>
    </div>
  );
}
