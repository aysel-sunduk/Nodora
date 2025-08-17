// Yeni config yerine environment'tan okuma
const ENABLE_LOGGING = true;
const API_BASE_URL = import.meta.env.VITE_API_URL;

function getApiUrl(endpoint) {
  return `${API_BASE_URL}${endpoint}`;
}

import apiClient from './apiClient';


// Kullanıcı girişi
export const login = async (email, password) => {
  try {
    if (ENABLE_LOGGING) {
      console.log('🔍 Login attempt started');
      console.log('📧 Email:', email);
      console.log('🔗 API URL:', getApiUrl('/api/auth/login'));
      console.log('📤 Request payload:', { email, password });
    }

    const response = await apiClient.post(getApiUrl('/api/auth/login'), { email, password });

    if (ENABLE_LOGGING) {
      console.log('✅ Login successful:', response.data);
    }

    return response.data; // { token, user }
  } catch (error) {
    if (ENABLE_LOGGING) {
      console.error('❌ Login failed:', error);
      console.error('❌ Error details:', {
        message: error.message,
        status: error.response?.status,
        data: error.response?.data,
        config: error.config
      });
    }
    throw error;
  }
};

// Kayıt olma
export const register = async (userData) => {
  try {
    const response = await apiClient.post(getApiUrl('/api/auth/signup'), userData);
    if (ENABLE_LOGGING) {
      console.log('✅ Register successful:', response.data);
    }
    return response.data;
  } catch (error) {
    if (ENABLE_LOGGING) {
      console.error('❌ Register error:', error);
    }
    throw error;
  }
};

// Google OAuth testi
export const testGoogleOAuth = async () => {
  try {
    if (ENABLE_LOGGING) {
      console.log('🧪 Testing Google OAuth configuration...');
      console.log('🔗 Test endpoint:', getApiUrl('/api/auth/google/test'));
    }

    const response = await apiClient.get(getApiUrl('/api/auth/google/test'));

    if (ENABLE_LOGGING) {
      console.log('✅ Google OAuth test successful:', response.data);
    }

    return response.data;
  } catch (error) {
    if (ENABLE_LOGGING) {
      console.error('❌ Google OAuth test failed:', error);
    }
    throw error;
  }
};

// Google OAuth ile giriş
export const googleAuth = async (idToken) => {
  try {
    const payload = { idToken };

    if (ENABLE_LOGGING) {
      console.log('🔍 Google OAuth attempt started');
      console.log('🔗 API URL:', getApiUrl('/api/auth/google'));
      console.log('📤 Request payload:', payload);
    }

    const response = await apiClient.post(getApiUrl('/api/auth/google'), payload, {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json'
      }
    });

    if (ENABLE_LOGGING) {
      console.log('✅ Google OAuth successful:', response.data);
      console.log('📊 Response status:', response.status);
    }

    return response.data; // { token, roleId, isNewUser, ... }
  } catch (error) {
    const status = error.response?.status;

    if (ENABLE_LOGGING) {
      console.error('❌ Google OAuth failed:', error);
      console.error('❌ Error details:', {
        message: error.message,
        status,
        data: error.response?.data,
        method: error.config?.method,
        url: error.config?.url
      });
    }

    if (status === 404) {
      throw new Error('Google OAuth endpoint bulunamadı. Backend kontrol edin.');
    } else if (status === 500) {
      throw new Error('Sunucu hatası. Google OAuth yapılandırmasını gözden geçirin.');
    } else if (status === 400) {
      throw new Error('Geçersiz token. Lütfen Google token formatını kontrol edin.');
    } else {
      throw new Error(`Google OAuth hatası: ${error.response?.data?.message || error.message}`);
    }
  }
};

// Logout işlemi
export const logout = async () => {
  try {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    if (ENABLE_LOGGING) {
      console.log('🚪 Kullanıcı çıkışı yapıldı.');
    }

    return { success: true };
  } catch (error) {
    console.warn('Logout error:', error.message);
    throw error;
  }
};

// Oturum geçerli mi kontrolü
export const checkAuth = async () => {
  try {
    const response = await apiClient.get(getApiUrl('/api/auth/check'));
    return response.data;
  } catch (error) {
    throw new Error('Oturum doğrulama başarısız');
  }
};
