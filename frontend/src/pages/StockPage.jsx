import { useEffect, useState } from 'react';
import api, { getErrorMessage } from '../api/client';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

export default function StockPage({ direction }) {
  const incoming = direction === 'incoming';
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [form, setForm] = useState({ productId: '', supplierId: '', quantity: '', recipient: '', notes: '' });
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    Promise.all([api.get('/products', { params: { size: 500 } }), incoming ? api.get('/suppliers', { params: { size: 500 } }) : Promise.resolve({ data: { content: [] } })])
      .then(([p, s]) => { setProducts(p.data.content || []); setSuppliers(s.data.content || []); })
      .catch((error) => setNotice({ type: 'danger', message: getErrorMessage(error) }));
  }, [incoming]);
  async function submit(event) {
    event.preventDefault(); setBusy(true);
    const payload = { productId: Number(form.productId), quantity: Number(form.quantity), notes: form.notes || null };
    if (incoming) payload.supplierId = Number(form.supplierId);
    else payload.recipient = form.recipient;
    try {
      const { data } = await api.post(`/stock/${direction}`, payload);
      setNotice({ type: 'success', message: `${data.quantity} units ${incoming ? 'received' : 'issued'} successfully` });
      setForm({ productId: '', supplierId: '', quantity: '', recipient: '', notes: '' });
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
    finally { setBusy(false); }
  }
  return <>
    <PageHeader title={incoming ? 'Receive stock' : 'Issue stock'} subtitle={incoming ? 'Record a delivery and update available quantities.' : 'Issue inventory to a person or department.'} />
    <Toast {...(notice || {})} onClose={() => setNotice(null)} />
    <section className="surface form-surface">
      <div className="form-intro"><div className={`metric-icon ${incoming ? 'green' : 'purple'}`}>{incoming ? '↓' : '↑'}</div><div><h2>{incoming ? 'Incoming stock details' : 'Outgoing stock details'}</h2><p>All movements are stored in the transaction history.</p></div></div>
      <form onSubmit={submit} className="form-grid stock-form">
        <label>Product<select className="form-select" required value={form.productId} onChange={(e) => setForm({ ...form, productId: e.target.value })}><option value="">Choose product…</option>{products.filter((p) => p.active).map((p) => <option key={p.id} value={p.id}>{p.name} · {p.code} ({p.quantity} available)</option>)}</select></label>
        {incoming ? <label>Supplier<select className="form-select" required value={form.supplierId} onChange={(e) => setForm({ ...form, supplierId: e.target.value })}><option value="">Choose supplier…</option>{suppliers.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}</select></label>
          : <label>Recipient / department<input className="form-control" required value={form.recipient} onChange={(e) => setForm({ ...form, recipient: e.target.value })} placeholder="e.g. Facilities team" /></label>}
        <label>Quantity<input className="form-control" required type="number" min="1" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} /></label>
        <label className="wide-field">Notes<textarea className="form-control" rows="3" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></label>
        <div className="wide-field form-submit"><button className="btn btn-primary" disabled={busy}>{busy ? 'Submitting…' : incoming ? 'Record incoming stock' : 'Record stock issue'}</button></div>
      </form>
    </section>
  </>;
}
