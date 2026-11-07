import { FiUser } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';

export default function ProfilePage() {
  const { user } = useAuth();
  return <>
    <PageHeader title="My profile" subtitle="Your account information and access level." />
    <section className="surface profile-card">
      <div className="profile-large-avatar"><FiUser /></div>
      <div className="profile-details"><span className="eyebrow">ACCOUNT</span><h2>{user.fullName}</h2><p>{user.email}</p>
        <div className="profile-role-list">{user.roles.map((role) => <span className="status-pill success" key={role}>{role}</span>)}</div>
        <div className="profile-access-note">Your account and role assignments are managed by an administrator.</div>
      </div>
    </section>
  </>;
}
