// src/components/ProtectedRoute.jsx

import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth'; // Eğer useAuth hook'un yoksa useContext(AuthContext) kullan

const ProtectedRoute = ({ children, requireAdmin = false }) => {
  const { token, user, loading } = useAuth();

  if (loading) {
    return (
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        fontSize: '1.2rem',
        color: '#666'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{ marginBottom: '1rem' }}>Yükleniyor...</div>
          <div style={{ fontSize: '0.9rem', color: '#999' }}>
            Backend bağlantısı kontrol ediliyor
          </div>
        </div>
      </div>
    );
  }

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && user?.role?.toUpperCase() !== "ADMIN") {
    return <Navigate to="/not-authorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
