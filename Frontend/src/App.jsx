import 'bootstrap/dist/css/bootstrap.min.css';
import React, { createContext, useEffect, useMemo, useState, useContext } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  useLocation,
} from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { useAuth } from "./hooks/useAuth";
import { GoogleOAuthProvider } from '@react-oauth/google';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import './App.css';

// Sayfalar
import Admin from './pages/Admin';
import AdminDashboard from './pages/AdminDashboard';
import AdminWorkspaces from './pages/AdminWorkspaces';
import AdminLogs from './pages/AdminLogs';
import Dashboard from './pages/Dashboard';
import ForgotPassword from './pages/ForgotPassword';
import Login from './pages/LoginForm';
import NotFound from './pages/NotFound';
import NotAuthorized from './pages/NotAuthorized.jsx';
import Register from './pages/Register';
import SettingsDrawerWrapper from './pages/SettingsDrawerWrapper';
import WorkSpace from './pages/Workspace.jsx';
import BoardPage from './pages/BoardPage.jsx';
import WorkspaceMember from './pages/WorkspaceMember.jsx'; // Yeni eklenen bileşen
import Settings2 from './pages/Settings2';
import BoardMember from './pages/BoardMember.jsx';

// Bileşenler
import ProtectedRoute from './components/ProtectedRoute';

export const ThemeContext = createContext();
const queryClient = new QueryClient();

const darkTheme = {
  background: "#18191A",
  color: "#E4E6EB",
};

const lightTheme = {
  background: "#f4f6fa",
  color: "#23272f",
};

function AppContent() {
  const { user, loading } = useAuth();
  const location = useLocation();
  const { theme } = useContext(ThemeContext);

  const isLoginPage = location.pathname === '/login' || location.pathname === '/';
  const isAdminPage = location.pathname.startsWith('/admin');

  if (loading) {
    return <div style={{ padding: 20, textAlign: 'center' }}>Yükleniyor...</div>;
  }

  const appContainerStyle = isAdminPage || isLoginPage ? {} : { minHeight: "100vh" };
  const appContainerClass = isAdminPage || isLoginPage ? '' : (theme === "dark" ? "theme-dark" : "theme-light");

  return (
    <div
      className={appContainerClass}
      style={{ ...appContainerStyle }}
    >
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/register" element={<Register />} />
        <Route path="/not-authorized" element={<NotAuthorized />} />
        <Route path="/404" element={<NotFound />} />

        {/* Protected routes */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/workspace"
          element={
            <ProtectedRoute>
              <WorkSpace />
            </ProtectedRoute>
          }
        />
        <Route
          path="/workspace/:workspaceId/members"
          element={
            <ProtectedRoute>
              <WorkspaceMember />
            </ProtectedRoute>
          }
        />
        <Route
          path="/board/:boardId"
          element={
            <ProtectedRoute>
              <BoardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/workspace/:workspaceId/board/:boardId/members"
          element={
            <ProtectedRoute>
              <BoardMember />
            </ProtectedRoute>
          }
        />
        <Route
          path="/settings"
          element={
            <ProtectedRoute>
              <SettingsDrawerWrapper />
            </ProtectedRoute>
          }
        />
        <Route
          path="/settings2"
          element={
            <ProtectedRoute>
              <Settings2 />
            </ProtectedRoute>
          }
        />
        {/* Admin paneli rotaları */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute requireAdmin={true}>
              <Admin />
            </ProtectedRoute>
          }
        >
          <Route index element={<AdminDashboard />} />
          <Route path="workspaces" element={<AdminWorkspaces />} />
          <Route path="logs" element={<AdminLogs />} />
        </Route>

        {/* Catch-all */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
}

function App() {
  const [theme, setTheme] = useState("dark");
  const themeStyles = useMemo(() => (theme === "dark" ? darkTheme : lightTheme), [theme]);

  useEffect(() => {
    const isLoginPage = window.location.pathname === '/login' || window.location.pathname === '/';
    const isAdminPage = window.location.pathname.startsWith('/admin');

    if (!isLoginPage && !isAdminPage) {
      document.body.style.background = themeStyles.background;
      document.body.style.color = themeStyles.color;
    } else {
      document.body.style.background = '';
      document.body.style.color = '';
    }
  }, [themeStyles]);

  return (
    <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
      <Router>
        <AuthProvider>
          <ThemeContext.Provider value={{ theme, setTheme }}>
            <QueryClientProvider client={queryClient}>
              <AppContent />
            </QueryClientProvider>
          </ThemeContext.Provider>
        </AuthProvider>
      </Router>
    </GoogleOAuthProvider>
  );
}

export default App;
