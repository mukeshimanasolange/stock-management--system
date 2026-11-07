import { useEffect, useState } from 'react';
import { Bar, Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend } from 'chart.js';
import { FiArrowDownLeft, FiArrowUpRight, FiBox, FiClipboard, FiPackage, FiTruck, FiUsers, FiLayers } from 'react-icons/fi';
import api, { getErrorMessage } from '../api/client';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const cards = [
  ['totalProducts', 'Products', FiPackage, 'blue'],
  ['totalCategories', 'Categories', FiLayers, 'purple'],
  ['totalSuppliers', 'Suppliers', FiTruck, 'amber'],
  ['totalUsers', 'Users', FiUsers, 'blue'],
  ['availableStock', 'Units in stock', FiBox, 'green'],
  ['incomingStock', 'Incoming units', FiArrowDownLeft, 'green'],
  ['outgoingStock', 'Outgoing units', FiArrowUpRight, 'purple'],
  ['pendingRequests', 'Pending requests', FiClipboard, 'amber'],
  ['lowStockProducts', 'Low-stock products', FiBox, 'amber'],
];

export default function DashboardPage() {
  const [summary, setSummary] = useState({});
  const [movements, setMovements] = useState([]);
  const [recent, setRecent] = useState([]);
  const [low, setLow] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [error, setError] = useState('');
  useEffect(() => {
    Promise.all([
      api.get('/dashboard/summary'), api.get('/dashboard/stock-movements'),
      api.get('/dashboard/recent-transactions'), api.get('/dashboard/low-stock'),
      api.get('/products', { params: { size: 500 } }),
    ]).then(([s, m, r, l, p]) => {
      setSummary(s.data); setMovements(m.data); setRecent(r.data); setLow(l.data);
      const products = p.data.content || [];
      const groups = products.reduce((acc, item) => {
        acc[item.categoryName] = (acc[item.categoryName] || 0) + 1;
        return acc;
      }, {});
      setCategoryData(Object.entries(groups));
    }).catch((e) => setError(getErrorMessage(e)));
  }, []);
  const movementChart = {
    labels: movements.map((item) => item.date),
    datasets: [
      { label: 'Incoming', data: movements.map((item) => item.incoming), backgroundColor: '#22c58b', borderRadius: 5 },
      { label: 'Outgoing', data: movements.map((item) => item.outgoing), backgroundColor: '#7c71f7', borderRadius: 5 },
    ],
  };
  const categoryChart = {
    labels: categoryData.map(([name]) => name),
    datasets: [{ data: categoryData.map(([, count]) => count), backgroundColor: ['#6563e8', '#22c58b', '#f4ad49', '#43a5df', '#f27878', '#b48be3'], borderWidth: 0 }],
  };
  return <>
    <PageHeader title="Good day" subtitle="Here’s what’s happening with your inventory today." />
    {error && <Toast type="danger" message={error} />}
    <div className="metric-grid">{cards.map(([key, title, Icon, color]) => <div className="metric-card surface" key={key}>
      <div className={`metric-icon ${color}`}><Icon /></div><div className="metric-title">{title}</div><div className="metric-value">{summary[key] ?? '—'}</div>
      <div className="metric-foot">{key === 'pendingRequests' ? 'Awaiting review' : 'Current total'}</div>
    </div>)}</div>
    <div className="dashboard-grid">
      <section className="surface chart-card"><div className="section-heading"><div><h2>Stock movement</h2><p>Incoming and outgoing quantity</p></div><span className="chart-period">Recent activity</span></div>
        <div className="chart-wrap"><Bar data={movementChart} options={{ responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 7 } } }, scales: { x: { grid: { display: false } }, y: { beginAtZero: true, grid: { color: '#edf0f5' } } } }} /></div>
      </section>
      <section className="surface chart-card category-card"><div className="section-heading"><div><h2>Product categories</h2><p>Share by product count</p></div></div>
        <div className="doughnut-wrap">{categoryData.length ? <Doughnut data={categoryChart} options={{ cutout: '70%', plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 7 } } } }} /> : <div className="empty-state">No category data</div>}</div>
      </section>
      <section className="surface recent-card"><div className="section-heading"><div><h2>Recent transactions</h2><p>Latest stock activity</p></div></div>
        <div className="compact-list">{recent.slice(0, 6).map((tx) => <div className="compact-row" key={tx.id}>
          <div className={`movement-icon ${tx.type === 'INCOMING' ? 'positive' : 'negative'}`}>{tx.type === 'INCOMING' ? <FiArrowDownLeft /> : <FiArrowUpRight />}</div>
          <div className="compact-main"><strong>{tx.productName}</strong><span>{tx.recipient || tx.supplierName || tx.performedBy}</span></div>
          <div className="compact-amount">{tx.type === 'INCOMING' ? '+' : '−'}{tx.quantity}</div>
        </div>)}
          {!recent.length && <div className="empty-state">No transactions recorded yet</div>}</div>
      </section>
      <section className="surface low-stock-card"><div className="section-heading"><div><h2>Low-stock alerts</h2><p>Products at or below minimum</p></div><span className="alert-count">{low.length}</span></div>
        <div className="compact-list">{low.slice(0, 6).map((product) => <div className="low-stock-row" key={product.id}>
          <div><strong>{product.name}</strong><span>{product.code}</span></div><div className="stock-level"><b>{product.quantity}</b><span>min {product.minimumStock}</span></div>
        </div>)}{!low.length && <div className="empty-state">All stock levels look healthy</div>}</div>
      </section>
    </div>
    <div className="sr-only">Incoming total {summary.incomingStock ?? 0}; outgoing total {summary.outgoingStock ?? 0}; users {summary.totalUsers ?? 0}; categories {summary.totalCategories ?? 0}</div>
  </>;
}
