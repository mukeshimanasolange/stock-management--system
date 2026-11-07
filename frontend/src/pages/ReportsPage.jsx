import { useEffect, useState } from 'react';
import { FiDownload, FiPrinter } from 'react-icons/fi';
import api, { getErrorMessage } from '../api/client';
import PageHeader from '../components/PageHeader';
import Toast from '../components/Toast';

const reports = [
  { id: 'stock', label: 'Available stock', url: '/reports/stock' },
  { id: 'incoming', label: 'Incoming stock', url: '/reports/incoming' },
  { id: 'outgoing', label: 'Outgoing stock', url: '/reports/outgoing' },
  { id: 'low-stock', label: 'Low-stock items', url: '/reports/low-stock' },
];

function csvValue(value) {
  const text = value == null ? '' : String(value);
  return `"${text.replaceAll('"', '""')}"`;
}

export default function ReportsPage() {
  const [type, setType] = useState('stock');
  const [rows, setRows] = useState([]);
  const [search, setSearch] = useState('');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [notice, setNotice] = useState(null);
  const selected = reports.find((report) => report.id === type);

  async function load() {
    const params = {};
    if (search && type === 'stock') params.search = search;
    if (from) params.from = new Date(`${from}T00:00:00`).toISOString();
    if (to) params.to = new Date(`${to}T23:59:59`).toISOString();
    try {
      const { data } = await api.get(selected.url, { params });
      setRows(Array.isArray(data) ? data : data.content || []);
    } catch (error) { setNotice({ type: 'danger', message: getErrorMessage(error) }); }
  }
  useEffect(() => { load(); }, [type, search, from, to]);

  const headers = rows.length ? Object.keys(rows[0]) : [];
  function exportCsv() {
    const content = [headers, ...rows.map((row) => headers.map((header) => row[header]))]
      .map((line) => line.map(csvValue).join(',')).join('\r\n');
    const url = URL.createObjectURL(new Blob([content], { type: 'text/csv;charset=utf-8' }));
    const link = document.createElement('a'); link.href = url; link.download = `stockwise-${type}.csv`; link.click();
    URL.revokeObjectURL(url);
  }
  return <>
    <PageHeader title="Reports" subtitle="Review inventory and stock movement for your selected period."
      action={<div className="report-actions"><button className="btn btn-light" onClick={() => window.print()}><FiPrinter /> Print / PDF</button><button className="btn btn-primary" disabled={!rows.length} onClick={exportCsv}><FiDownload /> Export CSV</button></div>} />
    <Toast {...(notice || {})} onClose={() => setNotice(null)} />
    <section className="surface report-filter">
      <label>Report<select className="form-select" value={type} onChange={(e) => setType(e.target.value)}>{reports.map((item) => <option value={item.id} key={item.id}>{item.label}</option>)}</select></label>
      {type !== 'low-stock' && <><label>From<input className="form-control" type="date" value={from} onChange={(e) => setFrom(e.target.value)} /></label><label>To<input className="form-control" type="date" value={to} onChange={(e) => setTo(e.target.value)} /></label></>}
      {type === 'stock' && <label>Product search<input className="form-control" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Name or code" /></label>}
      <div className="report-result-count"><strong>{rows.length}</strong><span>rows</span></div>
    </section>
    <section className="surface table-surface report-table">
      <div className="table-responsive"><table className="table align-middle data-table"><thead><tr>{headers.map((header) => <th key={header}>{header.replace(/[A-Z]/g, (char) => ` ${char}`).replace(/^./, (c) => c.toUpperCase())}</th>)}</tr></thead>
        <tbody>{rows.map((row, i) => <tr key={row.id || i}>{headers.map((header) => <td key={header}>{row[header] == null ? '—' : typeof row[header] === 'object' ? JSON.stringify(row[header]) : String(row[header])}</td>)}</tr>)}
          {!rows.length && <tr><td className="empty-state" colSpan={Math.max(headers.length, 1)}>No data for this report</td></tr>}</tbody>
      </table></div>
    </section>
  </>;
}
