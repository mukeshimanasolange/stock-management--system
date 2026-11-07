export default function Toast({ message, type = 'success', onClose }) {
  if (!message) return null;
  return (
    <div className={`alert alert-${type} alert-dismissible fade show app-toast`} role="alert">
      {message}
      <button type="button" className="btn-close" aria-label="Close" onClick={onClose} />
    </div>
  );
}
