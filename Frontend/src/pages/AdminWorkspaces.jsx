import {
    Alert,
    Box,
    Card,
    CardContent,
    CircularProgress,
    Grid,
    Typography
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { ThemeContext } from '../App';
import '../components/css/Admin.css';
import { getAdminWorkspaces } from '../services/api';

const AdminWorkspaces = () => {
  const { t } = useTranslation();
  const { theme } = useContext(ThemeContext);

  const { 
    data: workspaces = [], 
    isLoading, 
    isError, 
    error, 
    refetch 
  } = useQuery({
    queryKey: ['adminWorkspaces'],
    queryFn: async () => {
      const data = await getAdminWorkspaces();
      console.log("API Yanıtı:", data);
      return data;
    },
    retry: 2
  });

  if (isLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <CircularProgress />
      </Box>
    );
  }

  if (isError) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
        <Alert severity="error">
          {t('errorFetchingData')}: {error.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography 
        variant="h4" 
        component="h1"
        sx={{ 
          mb: 4,
          color: theme === 'dark' ? '#ffffff' : '#333333',
          fontWeight: 700,
          fontSize: '1.8rem'
        }}
      >
        {t('workspaces')}
      </Typography>
      
      <Grid container spacing={3}>
        {workspaces.length > 0 ? (
          workspaces.map((workspace) => (
            <Grid item xs={12} sm={6} md={4} key={`${workspace.workspaceId}-${workspace.memberId}`}>
              <Card
                sx={{
                  backgroundColor: theme === 'dark' ? '#2a2a2a' : '#ffffff',
                  color: theme === 'dark' ? '#ffffff' : '#333333',
                  borderRadius: '12px',
                  boxShadow: '0 4px 20px rgba(0, 0, 0, 0.12)',
                  height: '100%',
                  minHeight: '180px',
                  display: 'flex',
                  flexDirection: 'column',
                  transition: 'transform 0.3s',
                  '&:hover': {
                    transform: 'scale(1.02)'
                  }
                }}
              >
                <CardContent sx={{ flexGrow: 1, p: 3 }}>
                  <Box display="flex" justifyContent="space-between" mb={2}>
                    <Typography 
                      variant="h6" 
                      sx={{ 
                        fontWeight: 600,
                        fontSize: '1.2rem',
                        color: theme === 'dark' ? '#bbdefb' : '#1976d2'
                      }}
                    >
                      {workspace.workspaceName}
                    </Typography>
                    <Typography 
                      variant="body1"
                      sx={{
                        fontWeight: 500,
                        color: theme === 'dark' ? '#a5d6a7' : '#388e3c'
                      }}
                    >
                      ID: {workspace.workspaceId}
                    </Typography>
                  </Box>

                  <Box display="flex" flexWrap="wrap" gap={2}>
                    <Box>
                      <Typography variant="body2" sx={{ color: theme === 'dark' ? '#bdbdbd' : '#616161' }}>
                        Üye ID
                      </Typography>
                      <Typography variant="h6" sx={{ fontWeight: 500 }}>
                        {workspace.memberId}
                      </Typography>
                    </Box>

                    <Box>
                      <Typography variant="body2" sx={{ color: theme === 'dark' ? '#bdbdbd' : '#616161' }}>
                        Rol
                      </Typography>
                      <Typography variant="h6" sx={{ fontWeight: 500 }}>
                        {workspace.roleName || workspace.role?.name || '-'}
                      </Typography>
                    </Box>

                    <Box>
                      <Typography variant="body2" sx={{ color: theme === 'dark' ? '#bdbdbd' : '#616161' }}>
                        Rol ID
                      </Typography>
                      <Typography variant="h6" sx={{ fontWeight: 500 }}>
                        {workspace.roleId || workspace.role?.id || '-'}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))
        ) : (
          <Grid item xs={12}>
            <Typography 
              variant="body1" 
              sx={{ 
                color: theme === 'dark' ? '#aaaaaa' : '#666666',
                textAlign: 'center',
                py: 4,
                fontStyle: 'italic'
              }}
            >
              {t('noWorkspacesFound')}
            </Typography>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};

export default AdminWorkspaces;