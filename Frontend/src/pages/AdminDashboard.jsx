// src/pages/AdminDashboard.jsx
import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { getAdminWorkspacesCount, getAdminUsersActiveCount } from '../services/api';
import '../components/css/Admin.css';

const AdminDashboard = () => {
    const { t } = useTranslation();

    const { data: workspacesCount, isLoading: isWorkspacesLoading, error: workspacesError } = useQuery({
        queryKey: ['adminWorkspacesCount'],
        queryFn: getAdminWorkspacesCount,
    });

    const { data: usersActiveCount, isLoading: isUsersLoading, error: usersError } = useQuery({
        queryKey: ['adminUsersActiveCount'],
        queryFn: getAdminUsersActiveCount,
    });

    if (isWorkspacesLoading || isUsersLoading) {
        return <div className="loading-container">{t('loading')}</div>;
    }

    if (workspacesError || usersError) {
        return <div className="error-container">{t('errorFetchingData')}</div>;
    }

    return (
        <div className="admin-dashboard-content">
            <h1>{t('dashboard')}</h1>
            <div className="dashboard-cards">
                <div className="dashboard-card">
                    <h2>{t('Total Workspaces')}</h2>
                    <p>{workspacesCount}</p>
                </div>
                <div className="dashboard-card">
                    <h2>{t('Active Users')}</h2>
                    <p>{usersActiveCount}</p>
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;