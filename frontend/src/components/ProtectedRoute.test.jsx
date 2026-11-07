import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { expect, test } from 'vitest';
import { AuthProvider } from '../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';

test('redirects unauthenticated visitors to the login page', async () => {
  localStorage.removeItem('stockwise.token');
  render(
    <AuthProvider>
      <MemoryRouter initialEntries={['/private']}>
        <Routes>
          <Route path="/login" element={<div>Login screen</div>} />
          <Route element={<ProtectedRoute />}>
            <Route path="/private" element={<div>Private screen</div>} />
          </Route>
        </Routes>
      </MemoryRouter>
    </AuthProvider>,
  );
  expect(await screen.findByText('Login screen')).toBeInTheDocument();
});
