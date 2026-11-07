import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import AppShell from './components/AppShell';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProductsPage from './pages/ProductsPage';
import SuppliersPage from './pages/SuppliersPage';
import UsersPage from './pages/UsersPage';
import StockPage from './pages/StockPage';
import RequestsPage from './pages/RequestsPage';
import ReportsPage from './pages/ReportsPage';
import ProfilePage from './pages/ProfilePage';

const managers = ['ADMIN', 'STOREKEEPER'];

export default function App() {
  return <AuthProvider><BrowserRouter><Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route element={<ProtectedRoute />}><Route element={<AppShell />}>
      <Route element={<ProtectedRoute roles={managers} />}><Route index element={<DashboardPage />} /></Route>
      <Route path="products" element={<ProductsPage />} />
      <Route element={<ProtectedRoute roles={managers} />}>
        <Route path="suppliers" element={<SuppliersPage />} />
        <Route path="stock/incoming" element={<StockPage direction="incoming" />} />
        <Route path="stock/outgoing" element={<StockPage direction="outgoing" />} />
        <Route path="reports" element={<ReportsPage />} />
      </Route>
      <Route element={<ProtectedRoute roles={['ADMIN']} />}><Route path="users" element={<UsersPage />} /></Route>
      <Route path="requests" element={<RequestsPage />} />
      <Route path="profile" element={<ProfilePage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Route></Route>
  </Routes></BrowserRouter></AuthProvider>;
}
