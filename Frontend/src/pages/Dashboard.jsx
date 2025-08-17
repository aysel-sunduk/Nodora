// src/pages/Dashboard.jsx

import React from "react";
import { useNavigate } from "react-router-dom"; // useNavigate'ı import et

const Dashboard = () => {
  const navigate = useNavigate(); // useNavigate hook'unu kullan

  const handleOpenSettings = () => {
    // Ayarlar butonuna tıklandığında /settings rotasına yönlendir.
    navigate("/settings");
  };

  return (
    <div style={{ padding: 20 }}>
      <h1>Hoşgeldiniz!</h1>
      <p>Dashboard sayfasına başarıyla giriş yaptınız.</p>
      
      {/* Ayarlar menüsünü açacak buton */}
      <button onClick={handleOpenSettings} style={{ marginTop: 20 }}>
        Ayarlar
      </button>
    </div>
  );
};

export default Dashboard;