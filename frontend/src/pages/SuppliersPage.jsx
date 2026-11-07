import ResourcePage from '../components/ResourcePage';

const fields = [
  { name: 'name', label: 'Supplier name' }, { name: 'companyName', label: 'Company name', required: false },
  { name: 'phone', label: 'Phone', required: false }, { name: 'email', label: 'Email', type: 'email', required: false },
  { name: 'address', label: 'Address', type: 'textarea', wide: true, required: false },
  { name: 'active', label: 'Status', type: 'checkbox', defaultValue: true },
];
const columns = [
  { key: 'name', label: 'Supplier' }, { key: 'companyName', label: 'Company' },
  { key: 'phone', label: 'Phone' }, { key: 'email', label: 'Email' }, { key: 'address', label: 'Address' },
];
export default function SuppliersPage() {
  return <ResourcePage title="Suppliers" subtitle="Keep supplier details up to date for incoming stock." endpoint="/suppliers" fields={fields} columns={columns} />;
}
