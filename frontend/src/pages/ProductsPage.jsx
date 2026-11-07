import ResourcePage from '../components/ResourcePage';

const columns = [
  { key: 'name', label: 'Product' },
  { key: 'code', label: 'Code' },
  { key: 'categoryName', label: 'Category' },
  { key: 'unitPrice', label: 'Unit price', render: (p) => `$${Number(p.unitPrice).toFixed(2)}` },
  { key: 'quantity', label: 'Available' },
  { key: 'minimumStock', label: 'Minimum' },
  { key: 'active', label: 'Status', render: (p) => <span className={`status-pill ${p.active ? 'success' : 'muted-pill'}`}>{p.active ? 'Active' : 'Inactive'}</span> },
];
const fields = [
  { name: 'name', label: 'Product name', required: true },
  { name: 'code', label: 'Product code', required: true },
  { name: 'categoryId', label: 'Category', type: 'select', optionsEndpoint: '/categories', required: true },
  { name: 'unitPrice', label: 'Unit price', type: 'number', min: '0', defaultValue: 0 },
  { name: 'minimumStock', label: 'Minimum stock level', type: 'number', min: '0', defaultValue: 0 },
  { name: 'active', label: 'Status', type: 'checkbox', defaultValue: true },
  { name: 'description', label: 'Description', type: 'textarea', wide: true, required: false },
];

export default function ProductsPage() {
  return <ResourcePage title="Products" subtitle="Manage your catalog and keep an eye on available inventory." endpoint="/products" fields={fields} columns={columns} />;
}
