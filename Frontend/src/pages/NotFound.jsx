import React from "react";
import { Link } from "react-router-dom";

const NotFound = () => {
  return (
    <div style={styles.container}>
      <div style={styles.background}>
        <div style={styles.content}>
          <div style={styles.errorCode}>404</div>
          <h1 style={styles.title}>Sayfa Bulunamadı</h1>
          <p style={styles.message}>
            Üzgünüz, aradığınız sayfa mevcut değil veya taşınmış olabilir.
          </p>
          <div style={styles.buttonContainer}>
            <Link to="/" style={styles.primaryButton} className="not-found-primary-btn">
              Ana Sayfaya Dön
            </Link>
            <Link to="/login" style={styles.secondaryButton} className="not-found-secondary-btn">
              Giriş Yap
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

const styles = {
  container: {
    minHeight: "100vh",
    width: "100vw",
    position: "fixed",
    top: 0,
    left: 0,
    zIndex: 0,
    background: "linear-gradient(120deg, #002d4d, #124478, #5962af, #93a9c8, #808cb1)",
    backgroundSize: "400% 400%",
    animation: "gradientMove 18s ease-in-out infinite",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  background: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "100%",
    height: "100%",
  },
  content: {
    background: "rgba(101, 98, 96, 0.5)",
    padding: "48px 36px 32px 36px",
    borderRadius: "16px",
    boxShadow: "0 8px 32px 0 rgba(195, 196, 206, 0.18)",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    minWidth: "380px",
    position: "relative",
    textAlign: "center",
  },
  errorCode: {
    fontSize: "8rem",
    fontWeight: "bold",
    color: "#4f8cff",
    marginBottom: "1rem",
    textShadow: "0 4px 8px rgba(79, 140, 255, 0.3)",
    lineHeight: 1,
  },
  title: {
    color: "#fff",
    fontSize: "1.7rem",
    fontWeight: "bold",
    marginBottom: "1rem",
    letterSpacing: "1px",
  },
  message: {
    color: "#caced4",
    fontSize: "1.1rem",
    marginBottom: "2rem",
    lineHeight: 1.5,
    maxWidth: "400px",
  },
  buttonContainer: {
    display: "flex",
    gap: "1rem",
    justifyContent: "center",
    flexWrap: "wrap",
    width: "100%",
  },
  primaryButton: {
    textDecoration: "none",
    color: "#fff",
    background: "linear-gradient(90deg, #4f8cff 60%, #6c63ff 100%)",
    padding: "13px 24px",
    borderRadius: "24px",
    fontSize: "1.1rem",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "all 0.2s",
    boxShadow: "0 2px 8px rgba(34,197,94,0.15)",
    letterSpacing: "1px",
    border: "none",
    display: "inline-block",
  },
  secondaryButton: {
    textDecoration: "none",
    color: "#4f8cff",
    background: "transparent",
    padding: "13px 24px",
    borderRadius: "24px",
    fontSize: "1.1rem",
    fontWeight: "bold",
    cursor: "pointer",
    transition: "all 0.2s",
    letterSpacing: "1px",
    border: "2px solid #4f8cff",
    display: "inline-block",
  },
};

// CSS animation için global stil ekle
const styleSheet = document.createElement("style");
styleSheet.textContent = `
  @keyframes gradientMove {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
  }
  
  .not-found-primary-btn:hover {
    background: linear-gradient(90deg, #6c63ff 60%, #4f8cff 100%) !important;
    box-shadow: 0 4px 16px rgba(34,197,94,0.25) !important;
    transform: translateY(-2px);
  }
  
  .not-found-secondary-btn:hover {
    background: rgba(79, 140, 255, 0.1) !important;
    color: #6c63ff !important;
    border-color: #6c63ff !important;
    transform: translateY(-2px);
  }
`;

// Stil zaten eklenmişse tekrar ekleme
if (!document.querySelector('style[data-notfound-styles]')) {
  styleSheet.setAttribute('data-notfound-styles', 'true');
  document.head.appendChild(styleSheet);
}

export default NotFound;