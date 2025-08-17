import React from "react";
import { Link } from "react-router-dom";
import { useAuth } from '../hooks/useAuth';
import '../components/css/NotAuthorized.css';

const NotAuthorized = () => {
  const { user } = useAuth();

  return (
    <div className="not-authorized-container">
      <div className="not-authorized-background">
        <div className="not-authorized-content">
          <div className="not-authorized-error-code">403</div>
          <h1 className="not-authorized-title">Yetkisiz Erişim</h1>
          <p className="not-authorized-message">
            Bu sayfaya erişim yetkiniz bulunmamaktadır.
            {user && <span className="not-authorized-user-info"> Kullanıcı: {user.email}</span>}
          </p>
          <div className="not-authorized-button-container">
            <Link to="/dashboard" className="not-authorized-primary-btn">
              Ana Sayfaya Dön
            </Link>
            <Link to="/login" className="not-authorized-secondary-btn">
              Giriş Yap
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default NotAuthorized;