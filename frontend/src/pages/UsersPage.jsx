import ResourcePage from '../components/ResourcePage';

const fields = [
  { name: 'fullName', label: 'Full name' }, { name: 'email', label: 'Email', type: 'email' },
  { name: 'password', label: 'Password', type: 'password', help: 'At least 8 characters. Leave blank when keeping the current password.' },
  { name: 'roles', label: 'Roles', type: 'roles', placeholder: 'ADMIN, STOREKEEPER or STAFF', help: 'Separate multiple roles with commas.' },
  { name: 'active', label: 'Status', type: 'checkbox', defaultValue: true },
];
const columns = [
  { key: 'fullName', label: 'Name' }, { key: 'email', label: 'Email' },
  { key: 'roles', label: 'Roles', render: (u) => u.roles.join(', ') },
  { key: 'active', label: 'Status', render: (u) => <span className={`status-pill ${u.active ? 'success' : 'muted-pill'}`}>{u.active ? 'Active' : 'Inactive'}</span> },
];
export default function UsersPage() {
  return <ResourcePage title="Users" subtitle="Manage access and roles across your organization." endpoint="/users" fields={fields} columns={columns} searchable={false} />;
}
