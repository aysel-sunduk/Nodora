import { Button, CircularProgress, Paper, TextField } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';
import { ThemeContext } from '../App';
import '../components/css/Admin.css';
import '../components/css/Table.css';
import { getLogs } from '../services/api';

const AdminLogs = () => {
    const { t } = useTranslation();
    const { theme } = useContext(ThemeContext);
    const [logs, setLogs] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    // State to manage filters
    const [filters, setFilters] = useState({
        source: '',
        logLevel: '',
        memberId: ''
    });
    // New state to hold the userRole from localStorage
    const [userRole, setUserRole] = useState('');

    // Main function to fetch logs when the page first loads
    useEffect(() => {
        // Get user data as a JSON string from localStorage
        const userFromStorage = localStorage.getItem('user');
        if (userFromStorage) {
            try {
                // Parse the JSON string and get the role
                const userObject = JSON.parse(userFromStorage);
                console.log("User Role from localStorage:", userObject.role);
                setUserRole(userObject.role);
            } catch (error) {
                console.error("User data parsing error:", error);
                toast.error("User data could not be read. Please log in again.");
                setUserRole('');
            }
        } else {
            console.warn("'user' not found in localStorage. Please check the login process.");
            setUserRole('');
        }
    }, []);

    // Second useEffect to fetch logs when the userRole state is updated
    useEffect(() => {
        // Fetch logs if role information is available
        if (userRole === 'admin') {
            fetchLogs();
        } else if (userRole && userRole !== 'admin') {
            // Close the loading screen for non-admin users
            setIsLoading(false);
            toast.error("You do not have permission to view logs.");
        }
    }, [userRole]);

    // Log fetching function, runs only for users with admin role
    const fetchLogs = async (appliedFilters = filters) => {
        setIsLoading(true);

        try {
            // Create filters to be sent to the backend by filtering out empty strings
            const finalFilters = {};
            for (const key in appliedFilters) {
                if (appliedFilters[key] !== '') {
                    finalFilters[key] = appliedFilters[key];
                }
            }
            
            // Construct URL search params from the finalFilters object
            const queryString = new URLSearchParams(finalFilters).toString();
            const apiUrl = `http://localhost:8080/logs?${queryString}`;
            
            // Log the final filters and the constructed URL to the console
            console.log("Backend'e gönderilen son filtreler:", finalFilters);
            console.log("Oluşturulan API URL'si:", apiUrl);
            
            // Call to your backend API. Assumes `getLogs` handles the full URL.
            const fetchedLogs = await getLogs(finalFilters);
            
            // Log the data received from the backend
            console.log("Backend'den alınan loglar:", fetchedLogs);
            
            setLogs(fetchedLogs);
            toast.success(t('logs_loaded_successfully'));
        } catch (error) {
            console.error('An error occurred while fetching logs:', error);
            if (error.response) {
                const status = error.response.status;
                if (status === 401 || status === 403) {
                    toast.error(t('yetki_hatasi_mesaji') || "Authorization error. Please ensure you are logged in.");
                } else if (status === 404) {
                    toast.error(t('api_bulunamadi_mesaji') || "API endpoint not found. Is your backend server running?");
                } else if (status >= 500) {
                    toast.error(t('sunucu_hatasi_mesaji') || "A server error occurred. Check backend logs, especially database queries.");
                } else {
                    toast.error(t('genel_hata_mesaji') || `An error occurred: ${error.response.data?.message || error.message}`);
                }
            } else if (error.request) {
                toast.error(t('ag_hatasi_mesaji') || "Network error: Backend server is unreachable.");
            } else {
                toast.error(t('beklenmedik_hata_mesaji') || "An unexpected error occurred while loading logs.");
            }
        } finally {
            setIsLoading(false);
            console.log("Log fetching process completed.");
        }
    };

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prevFilters => ({
            ...prevFilters,
            [name]: value
        }));
    };

    const handleApplyFilters = () => {
        if (userRole !== 'admin') {
            toast.error("You do not have permission.");
            return;
        }
        console.log("Applying filters:", filters);
        fetchLogs(filters);
    };

    const handleResetFilters = () => {
        if (userRole !== 'admin') {
            toast.error("You do not have permission.");
            return;
        }
        const resetFilters = { source: '', logLevel: '', memberId: '' };
        setFilters(resetFilters);
        fetchLogs(resetFilters);
    };

    if (isLoading && userRole === '') {
        return (
            <div className={`admin-logs-content ${theme}`}>
                <CircularProgress />
                <p>{t('loading_logs')}</p>
            </div>
        );
    }
    
    // Render content only if the user has the 'admin' role
    if (userRole !== 'admin') {
        return (
            <div className={`admin-logs-content ${theme}`}>
                <h1 className="admin-logs-title" style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>
                    {t('logs')}
                </h1>
                <p style={{ color: theme === 'dark' ? '#ffffff' : '#000000', textAlign: 'center', marginTop: '20px' }}>
                    You do not have sufficient permissions to view this page.
                </p>
            </div>
        );
    }

    return (
        <div className={`admin-logs-content ${theme}`}>
            <h1 className="admin-logs-title" style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>
                {t('logs')}
            </h1>

            <Paper 
                className="log-filters"
                sx={{ 
                    p: 2,
                    mb: 2,
                    backgroundColor: theme === 'dark' ? '#1e1e1e' : '#ffffff',
                    color: theme === 'dark' ? '#ffffff' : '#000000'
                }}
            >
                <TextField
                    type="text"
                    name="source"
                    label={t('filter_by_source')}
                    value={filters.source}
                    onChange={handleFilterChange}
                    variant="outlined"
                    size="small"
                    sx={{ mr: 2, width: 200 }}
                />
                <TextField
                    type="text"
                    name="logLevel"
                    label={t('filter_by_level')}
                    value={filters.logLevel}
                    onChange={handleFilterChange}
                    variant="outlined"
                    size="small"
                    sx={{ mr: 2, width: 200 }}
                />
                <TextField
                    type="number"
                    name="memberId"
                    label={t('filter_by_member_id')}
                    value={filters.memberId}
                    onChange={handleFilterChange}
                    variant="outlined"
                    size="small"
                    sx={{ mr: 2, width: 200 }}
                />
                <Button 
                    variant="contained" 
                    onClick={handleApplyFilters}
                    sx={{ mr: 2 }}
                >
                    {t('filter')}
                </Button>
                <Button 
                    variant="outlined" 
                    onClick={handleResetFilters}
                >
                    {t('reset')}
                </Button>
            </Paper>
            
            {isLoading ? (
                <div style={{ textAlign: 'center', marginTop: '20px' }}>
                    <CircularProgress />
                    <p>{t('loading_logs')}</p>
                </div>
            ) : logs.length > 0 ? (
                <Paper
                    sx={{ 
                        backgroundColor: theme === 'dark' ? '#1e1e1e' : '#ffffff',
                        color: theme === 'dark' ? '#ffffff' : '#000000'
                    }}
                >
                    <table className={`log-table ${theme}`}>
                        <thead>
                            <tr style={{ 
                                backgroundColor: theme === 'dark' ? '#2d2d2d' : '#f5f5f5'
                            }}>
                                <th style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{t('log_id')}</th>
                                <th style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{t('log_source')}</th>
                                <th style={{ color: 'dark' ? '#ffffff' : '#000000' }}>{t('log_level')}</th>
                                <th style={{ color: 'dark' ? '#ffffff' : '#000000' }}>{t('log_message')}</th>
                                <th style={{ color: 'dark' ? '#ffffff' : '#000000' }}>{t('log_timestamp')}</th>
                                <th style={{ color: 'dark' ? '#ffffff' : '#000000' }}>{t('log_member_id')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {logs.map(log => (
                                <tr 
                                    key={log.logId}
                                    style={{ 
                                        backgroundColor: theme === 'dark' ? '#1e1e1e' : '#ffffff',
                                        '&:hover': {
                                            backgroundColor: theme === 'dark' ? '#2d2d2d' : '#f5f5f5'
                                        }
                                    }}
                                >
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{log.logId}</td>
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{log.source}</td>
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{log.level}</td>
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{log.message}</td>
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{new Date(log.timestamp).toLocaleString()}</td>
                                    <td style={{ color: theme === 'dark' ? '#ffffff' : '#000000' }}>{log.memberId || '-'}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </Paper>
            ) : (
                <p style={{ color: theme === 'dark' ? '#ffffff' : '#000000', textAlign: 'center', marginTop: '20px' }}>
                    No records found. Please check your filters or reset them.
                </p>
            )}
        </div>
    );
};

export default AdminLogs;
