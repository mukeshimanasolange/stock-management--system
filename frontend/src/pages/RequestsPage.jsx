import { useEffect, useState } from 'react';
import { FiCheck, FiPlus, FiX } from 'react-icons/fi';
import api, { getErrorMessage } from '../api/client';
import { useAuth } from '../context/AuthContext';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

export default function RequestsPage() {
  const { user } = useAuth();
  const reviewer = user.roles.some((role) => ['ADMIN', 'STOREKEEPER'].includes(role));
  const [requests, setRequests] = useState([]);
  const [products, setProducts] = useState([]);
  const [items, setItems] = useState([{ productId: '', quantity: 1 }]);
  const [showForm, setShowForm] = useState(false);
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(false);

  async function load() {
    try {
      const { data } = await api.get('/requests');
      setRequests(data);
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
  }
  useEffect(() => {
    load();
    api.get('/products', { params: { size: 500 } })
      .then(({ data }) => setProducts((data.content || []).filter((p) => p.active)))
      .catch((error) => setNotice({ type: 'danger', message: getErrorMessage(error) }));
  }, []);

  async function createRequest(event) {
    event.preventDefault(); setBusy(true);
    try {
      await api.post('/requests', { items: items.map((item) => ({ productId: Number(item.productId), quantity: Number(item.quantity) })) });
      setShowForm(false); setItems([{ productId: '', quantity: 1 }]);
      setNotice({ type: 'success', message: 'Stock request submitted' });
      await load();
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
    finally { setBusy(false); }
  }
  async function action(request, actionName) {
    try {
      await api.put(`/requests/${request.id}/${actionName}`, actionName === 'approve' || actionName === 'reject' ? { notes: '' } : {});
      const result = { approve: 'approved', reject: 'rejected', issue: 'issued' }[actionName];
      setNotice({ type: 'success', message: `Request ${result}` });
      await load();
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
  }

  return <>
    <PageHeader title="Stock requests" subtitle={reviewer ? 'Review requests and fulfill approved stock.' : 'Request products and follow their status.'}
      action={<button className="btn btn-primary" onClick={() => setShowForm(true)}><FiPlus /> New request</button>} />
    <Toast {...(notice || {})} onClose={() => setNotice(null)} />
    <section className="surface table-surface">
      <div className="table-toolbar"><div><h2>Request history</h2><span className="muted">{requests.length} requests</span></div></div>
      <div className="request-list">{requests.map((request) => <article className="request-card" key={request.id}>
        <div className="request-main">
          <div className="request-title"><strong>Request #{request.id}</strong><span className={`status-pill status-${request.status.toLowerCase()}`}>{request.status}</span></div>
          <div className="request-meta">{request.requestedBy} · {new Date(request.requestedAt).toLocaleString()}</div>
          <div className="request-items">{request.items.map((item) => <span key={item.productId}>{item.productName} <b>× {item.quantity}</b></span>)}</div>
          {request.reviewNotes && <p className="request-note">{request.reviewNotes}</p>}
        </div>
        {reviewer && <div className="request-actions">
          {request.status === 'PENDING' && <>
            <button className="btn btn-light btn-sm" onClick={() => action(request, 'reject')}><FiX /> Reject</button>
            <button className="btn btn-primary btn-sm" onClick={() => action(request, 'approve')}><FiCheck /> Approve</button>
          </>}
          {request.status === 'APPROVED' && <button className="btn btn-primary btn-sm" onClick={() => action(request, 'issue')}><FiCheck /> Issue stock</button>}
        </div>}
      </article>)}
      {!requests.length && <div className="empty-state">No stock requests yet</div>}</div>
    </section>
    {showForm && <div className="modal-backdrop-custom"><form className="dialog-card" onSubmit={createRequest}>
      <div className="dialog-heading"><div><span className="eyebrow">INVENTORY REQUEST</span><h2>Request products</h2></div><button type="button" className="icon-btn" onClick={() => setShowForm(false)}>×</button></div>
      <div className="request-form-items">{items.map((item, index) => <div className="request-form-row" key={index}>
        <label>Product<select className="form-select" required value={item.productId} onChange={(e) => setItems(items.map((line, i) => i === index ? { ...line, productId: e.target.value } : line))}>
          <option value="">Select product…</option>{products.map((p) => <option key={p.id} value={p.id}>{p.name} · {p.code}</option>)}
        </select></label>
        <label>Quantity<input className="form-control" type="number" min="1" required value={item.quantity} onChange={(e) => setItems(items.map((line, i) => i === index ? { ...line, quantity: e.target.value } : line))} /></label>
        {items.length > 1 && <button type="button" className="icon-btn danger" aria-label="Remove item" onClick={() => setItems(items.filter((_, i) => i !== index))}><FiX /></button>}
      </div>)}
      <button type="button" className="btn btn-light btn-sm" onClick={() => setItems([...items, { productId: '', quantity: 1 }])}><FiPlus /> Add another product</button></div>
      <div className="dialog-actions"><button type="button" className="btn btn-light" onClick={() => setShowForm(false)}>Cancel</button><button className="btn btn-primary" disabled={busy}>{busy ? 'Submitting…' : 'Submit request'}</button></div>
    </form></div>}
  </>;
}
