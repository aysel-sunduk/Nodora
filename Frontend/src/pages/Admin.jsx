import React, { useEffect, useState, useContext } from 'react';
import { useNavigate, Link, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import '../components/css/Admin.css';
import { ThemeContext } from '../App';
import { useTranslation } from 'react-i18next';

import Settings from './Settings';
import { IconButton } from '@mui/material';
import SettingsIcon from '@mui/icons-material/Settings';

const Admin = () => {
    const { user, token, loading } = useAuth();
    const navigate = useNavigate();
    const [activeMenu, setActiveMenu] = useState('dashboard'); // Başlangıç menüsü tekrar 'dashboard' olarak ayarlandı
    const [isSettingsOpen, setIsSettingsOpen] = useState(false);
    
    const { theme } = useContext(ThemeContext); 
    const { t } = useTranslation();

    useEffect(() => {
        if (!loading) {
            if (!token) {
                navigate('/login');
            } else if (user?.role?.toUpperCase() !== 'ADMIN') {
                navigate('/not-authorized');
            }
        }
    }, [token, user, loading, navigate]);

    if (loading || !user || user.role?.toUpperCase() !== 'ADMIN') {
        return <div>{t('loading')}</div>;
    }

    const handleOpenSettings = () => {
        setIsSettingsOpen(true);
    };

    const handleCloseSettings = () => {
        setIsSettingsOpen(false);
    };

    return (
        <div className={`admin-container ${theme}`}> 
            <aside className="admin-sidebar">
                <div className="sidebar-header">
                    <h2>{t('adminPanel')}</h2>
                </div>
                <nav className="sidebar-nav">
                    <Link 
                        to="/admin" 
                        onClick={() => setActiveMenu('dashboard')} 
                        className={`nav-link ${activeMenu === 'dashboard' ? 'active' : ''}`}
                    >
                        {t('dashboard')}
                    </Link>
                    <Link 
                        to="/admin/workspaces" 
                        onClick={() => setActiveMenu('workspaces')} 
                        className={`nav-link ${activeMenu === 'workspaces' ? 'active' : ''}`}
                    >
                        {t('workspaces')}
                    </Link>
                    <Link 
                        to="/admin/logs" 
                        onClick={() => setActiveMenu('logs')} 
                        className={`nav-link ${activeMenu === 'logs' ? 'active' : ''}`}
                    >
                        {t('logs')}
                    </Link>
                </nav>
            </aside>
            
            <main className="admin-main">
                <div className="admin-main-header">
                    <IconButton onClick={handleOpenSettings} className="settings-button">
                        <SettingsIcon />
                    </IconButton>
                </div>
                <Outlet />
            </main>

            {isSettingsOpen && <Settings open={isSettingsOpen} onClose={handleCloseSettings} />}
        </div>
    );
};

export default Admin;