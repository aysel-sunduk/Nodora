// src/pages/SettingsDrawerWrapper.jsx

import React from 'react';
import { Drawer, Box, Typography, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

const SettingsDrawerWrapper = ({ open, onClose }) => {
    return (
        <Drawer anchor="right" open={open} onClose={onClose}>
            <Box
                sx={{
                    width: 300,
                    padding: 2,
                    backgroundColor: '#f0f2f5',
                    height: '100%',
                }}
                role="presentation"
            >
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 2 }}>
                    <Typography variant="h6">Ayarlar</Typography>
                    <IconButton onClick={onClose}>
                        <CloseIcon />
                    </IconButton>
                </Box>
                {/* Ayarlar içeriği buraya gelecek */}
                <Typography>Ayarlarınızı buradan yönetebilirsiniz.</Typography>
            </Box>
        </Drawer>
    );
};

export default SettingsDrawerWrapper;
