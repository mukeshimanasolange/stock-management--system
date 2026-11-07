import { useEffect, useMemo, useState } from 'react';
import { FiEdit2, FiPlus, FiSearch, FiTrash2 } from 'react-icons/fi';
import api, { getErrorMessage } from '../api/client';
import PageHeader from './PageHeader';
import Toast from './Toast';

function initialValues(fields) {
  return Object.fromEntries(fields.map((field) => [field.name, field.defaultValue ?? '']));
}

export default function ResourcePage({ title, subtitle, endpoint, fields, columns, searchable = true }) {
  const [rows, setRows] = useState([]);
  const [options, setOptions] = useState({});
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [form, setForm] = useState(null);
  const [editing, setEditing] = useState(null);
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState(null);

  const query = useMemo(() => ({ page, size: 10, ...(searchable && search ? { search } : {}) }), [page, search, searchable]);

  async function load() {
    try {
      const { data } = await api.get(endpoint, { params: query });
      setRows(Array.isArray(data) ? data : data.content || []);
      setTotalPages(data.totalPages || 1);
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
  }
  useEffect(() => { load(); }, [endpoint, page, search]);
  useEffect(() => {
    Promise.all(fields.filter((f) => f.optionsEndpoint).map((f) =>
      api.get(f.optionsEndpoint).then(({ data }) => [f.name, Array.isArray(data) ? data : data.content || []])))
      .then((pairs) => setOptions(Object.fromEntries(pairs)))
      .catch((error) => setNotice({ type: 'danger', message: getErrorMessage(error) }));
  }, [endpoint]);

  function openForm(row) {
    const next = initialValues(fields);
    if (row) fields.forEach((field) => {
      const value = row[field.name];
      next[field.name] = field.name === 'categoryId' ? row.categoryId : (value ?? '');
      if (field.type === 'roles') next[field.name] = (row.roles || []).join(',');
      if (field.type === 'password') next[field.name] = '';
    });
    setEditing(row || null);
    setForm(next);
  }
  async function submit(event) {
    event.preventDefault();
    setBusy(true);
    const payload = { ...form };
    fields.forEach((field) => {
      if (field.type === 'number' && payload[field.name] !== '') payload[field.name] = Number(payload[field.name]);
      if (field.type === 'roles') payload[field.name] = payload[field.name].split(',').map((role) => role.trim().toUpperCase()).filter(Boolean);
      if (field.type === 'password' && !payload[field.name]) delete payload[field.name];
    });
    try {
      if (editing) await api.put(`${endpoint}/${editing.id}`, payload);
      else await api.post(endpoint, payload);
      setForm(null); setNotice({ type: 'success', message: `${title.slice(0, -1)} ${editing ? 'updated' : 'created'}` });
      await load();
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
    finally { setBusy(false); }
  }
  async function remove(row) {
    if (!window.confirm(`Deactivate ${row.name || row.fullName || row.code || row.email}?`)) return;
    try {
      await api.delete(`${endpoint}/${row.id}`);
      setNotice({ type: 'success', message: 'Record deactivated' });
      await load();
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
  }

  return (
    <>
      <PageHeader title={title} subtitle={subtitle} action={<button className="btn btn-primary" onClick={() => openForm()}><FiPlus /> Add new</button>} />
      <Toast {...(notice || {})} onClose={() => setNotice(null)} />
      <section className="surface table-surface">
        <div className="table-toolbar">
          <div><h2>{title}</h2><span className="muted">{rows.length} records on this page</span></div>
          {searchable && <label className="search-box"><FiSearch /><input value={search} placeholder="Search..." onChange={(e) => { setPage(0); setSearch(e.target.value); }} /></label>}
        </div>
        <div className="table-responsive">
          <table className="table align-middle data-table">
            <thead><tr>{columns.map((col) => <th key={col.key}>{col.label}</th>)}<th className="text-end">Actions</th></tr></thead>
            <tbody>{rows.map((row) => <tr key={row.id}>{columns.map((col) => (
              <td key={col.key}>{col.render ? col.render(row) : (row[col.key] ?? '—')}</td>
            ))}<td className="text-end action-cell">
              <button className="icon-btn" title="Edit" onClick={() => openForm(row)}><FiEdit2 /></button>
              <button className="icon-btn danger" title="Deactivate" onClick={() => remove(row)}><FiTrash2 /></button>
            </td></tr>)}
            {!rows.length && <tr><td colSpan={columns.length + 1} className="empty-state">No records found</td></tr>}</tbody>
          </table>
        </div>
        <div className="pagination-row"><span>Page {page + 1} of {totalPages}</span><div>
          <button className="btn btn-light btn-sm" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <button className="btn btn-light btn-sm ms-2" disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div></div>
      </section>
      {form && <div className="modal-backdrop-custom" role="presentation" onMouseDown={(e) => e.target === e.currentTarget && setForm(null)}>
        <form className="dialog-card" onSubmit={submit}>
          <div className="dialog-heading"><div><span className="eyebrow">{editing ? 'UPDATE RECORD' : 'NEW RECORD'}</span><h2>{editing ? 'Edit' : 'Create'} {title.replace(/s$/, '').toLowerCase()}</h2></div><button type="button" className="icon-btn" onClick={() => setForm(null)}>×</button></div>
          <div className="form-grid">
            {fields.map((field) => <label className={field.wide ? 'wide-field' : ''} key={field.name}>{field.label}
              {field.type === 'select' ? <select required={field.required !== false} className="form-select" value={form[field.name]} onChange={(e) => setForm({ ...form, [field.name]: e.target.value })}>
                <option value="">Select…</option>{(options[field.name] || []).map((option) => <option value={option.id} key={option.id}>{option.name}</option>)}
              </select> : field.type === 'textarea' ? <textarea className="form-control" rows="3" value={form[field.name]} onChange={(e) => setForm({ ...form, [field.name]: e.target.value })} /> :
                field.type === 'checkbox' ? <span className="checkbox-field"><input type="checkbox" checked={Boolean(form[field.name])} onChange={(e) => setForm({ ...form, [field.name]: e.target.checked })} /> Active</span> :
                <input className="form-control" type={field.type === 'roles' ? 'text' : field.type || 'text'} required={field.required !== false && !(editing && field.type === 'password') && !(field.type === 'password' && editing)} min={field.min} maxLength={field.maxLength} value={form[field.name]} placeholder={field.placeholder} onChange={(e) => setForm({ ...form, [field.name]: e.target.value })} />}
              {field.help && <small className="text-muted">{field.help}</small>}
            </label>)}
          </div>
          <div className="dialog-actions"><button type="button" className="btn btn-light" onClick={() => setForm(null)}>Cancel</button><button className="btn btn-primary" disabled={busy}>{busy ? 'Saving…' : 'Save record'}</button></div>
        </form>
      </div>}
    </>
  );
}
