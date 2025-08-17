import { GoogleOAuthProvider } from '@react-oauth/google';
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import './i18n'; // i18n yapılandırma dosyası
import "./index.css";
const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;


const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <GoogleOAuthProvider clientId={clientId}>
      <App />
    </GoogleOAuthProvider>
  </React.StrictMode>
);
