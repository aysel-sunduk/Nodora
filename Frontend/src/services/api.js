import axios from 'axios';
import { toast } from 'react-toastify';

// API'nin temel URL'sini ve diğer konfigürasyonları ayarla
const API_BASE_URL = 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Hata yönetimi için interceptor
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Axios interceptor ile her istekte token'ı otomatik olarak ekle
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// --- Auth ---
export const login = (credentials) =>
  apiClient.post('/auth/login', credentials).then((res) => res.data);

export const register = (data) =>
  apiClient.post('/auth/signup', data).then((res) => res.data);

export const forgotPassword = (email) =>
  apiClient
    .post('/auth/forgot-password', { email })
    .then((res) => res.data)
    .catch((error) => {
      console.error('Forgot password error:', error.response?.data);
      throw new Error(
        error.response?.data?.message || 'Şifre sıfırlama isteği gönderilemedi'
      );
    });

export const resetPassword = async (currentPassword, newPassword) => {
  try {
    const response = await apiClient.post('/auth/reset-password', {
      currentPassword,
      newPassword,
      confirmPassword: newPassword,
    });
    return response.data;
  } catch (error) {
    const errorMessage =
      error.response?.data?.message ||
      'Şifre değiştirme işlemi sırasında bir hata oluştu.';
    console.error('API Error:', error.response);
    throw new Error(errorMessage);
  }
};

// --- Google Auth ---
export const googleAuth = (token) =>
  apiClient.post('/auth/google', { token }).then((res) => res.data);

export const testGoogleAuth = () =>
  apiClient.get('/auth/google/test').then((res) => res.data);

// --- Admin ---

export const getAdminDashboard = () =>
  apiClient.get('/api/admin/dashboard').then(res => res.data)

export const getAdminWorkspaces = () =>
  apiClient.get('/api/admin/workspaces').then(res => res.data)

export const getAdminWorkspacesCount = () =>
  apiClient.get('/api/admin/workspaces/count').then(res => res.data)

export const getAdminUsersActiveCount = () =>
  apiClient.get('/api/admin/users/active/count').then(res => res.data)

// --- Workspaces ---
// DÜZELTİLDİ
export const createWorkspace = async (data) => {
  try {
    const response = await apiClient.post('/api/workspaces', {
      memberId: Number(data.memberId),
      workspaceName: String(data.workspaceName).trim(),
    });
    return response.data;
  } catch (error) {
    console.error('API hatası:', {
      requestData: data,
      error: error.response?.data || error.message,
    });
    throw error;
  }
};

export const deleteWorkspace = async (workspaceId) => {
  try {
    const response = await apiClient.delete(`/api/workspaces/${workspaceId}`);
    return response.data;
  } catch (error) {
    console.error('Workspace silinirken hata oluştu:', error);
    throw error;
  }
};

export const getWorkspacesByMember = (memberId) =>
  apiClient.get(`/api/workspaces/member/${memberId}`).then((res) => res.data);

// --- Logs ---
export const getLogs = async (filters = {}) => {
  try {
    const params = new URLSearchParams();

    if (filters.source) {
      params.append('source', filters.source);
    }
    if (filters.logLevel) {
      params.append('logLevel', filters.logLevel);
    }
    if (filters.memberId) {
      params.append('memberId', filters.memberId);
    }

    const response = await apiClient.get('/api/v1/logs', { params });
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 403) {
      toast.error('Bu işlemi yapmak için yetkiniz yok.');
    } else {
      toast.error('Logları çekerken bir hata oluştu.');
    }
    console.error('API Hatası:', error);
    throw error;
  }
};

// --- Membership ---
export const createMembership = (data) =>
  apiClient.post('/api/membership', data).then((res) => res.data);

export const getMembershipsByWorkspace = (workspaceId) =>
  apiClient.get(`/api/membership/workspace/${workspaceId}`).then((res) => res.data);

export const deleteMembership = (id) =>
  apiClient.delete(`/api/membership/${id}`).then((res) => res.data);

// --- Workspace Members ---
export const updateWorkspaceMemberRole = (id, data) =>
  apiClient.put(`/api/workspace-members/${id}/role`, data).then((res) => res.data);

export const inviteWorkspaceMember = (data) =>
  apiClient.post('/api/workspace-members/invite', data).then((res) => res.data);

export const getWorkspaceMembers = (workspaceId) =>
  apiClient
    .get(`/api/workspace-members/workspace/${workspaceId}`)
    .then((res) => res.data);

export const getWorkspaceMember = (memberId) =>
  apiClient.get(`/api/workspace-members/member/${memberId}`).then((res) => res.data);

export const deleteWorkspaceMember = (id) =>
  apiClient.delete(`/api/workspace-members/${id}`).then((res) => res.data);

// --- Roles ---
export const getRole = (id) =>
  apiClient.get(`/api/roles/${id}`).then((res) => res.data);

export const updateRole = (id, data) =>
  apiClient.put(`/api/roles/${id}`, data).then((res) => res.data);

export const deleteRole = (id) =>
  apiClient.delete(`/api/roles/${id}`).then((res) => res.data);

export const getRoles = () => apiClient.get('/api/roles').then((res) => res.data);

export const createRole = (data) =>
  apiClient.post('/api/roles', data).then((res) => res.data);

// --- Boards ---
export const getBoardsByWorkspace = (workspaceId, memberId) => {
  return apiClient
    .get(`/api/boards/workspace/${workspaceId}`, { params: { memberId } })
    .then((res) => res.data)
    .catch((error) => {
      console.error('Boards fetch error:', error);
      throw new Error('Panolar alınırken hata oluştu');
    });
};

export const createBoard = async (boardData) => {
  try {
    const response = await apiClient.post('/api/boards', boardData);
    return response.data;
  } catch (error) {
    console.error('Board creation error:', error);
    throw new Error('Beklenmeyen bir hata oluştu.');
  }
};

export const updateBoard = (boardId, data) =>
  apiClient
    .put(`/api/boards/${boardId}`, {
      title: data.title,
      bgColor: data.bgColor,
      memberId: data.memberId,
      roleId: data.roleId,
    })
    .then((res) => res.data);

export const deleteBoard = (boardId, memberId) =>
  apiClient
    .delete(`/api/boards/${boardId}`, {
      params: { memberId },
    })
    .then((res) => res.data);

export const promoteBoardLeader = (boardId, memberId, requesterId) =>
  apiClient
    .post(`/api/boards/${boardId}/promote-leader`, null, {
      params: { memberId, requesterId },
    })
    .then((res) => res.data);

// --- Board Members ---
export const addBoardMember = (boardId, workspaceId, memberId, requesterId) =>
  apiClient
    .post('/api/board-members/add', null, {
      params: { boardId, workspaceId, memberId, requesterId },
    })
    .then((res) => res.data);

export const getBoardMembers = (boardId) =>
  apiClient
    .get('/api/board-members/list', {
      params: { boardId },
    })
    .then((res) => res.data);

export const removeBoardMember = (boardId, memberId, requesterId) =>
  apiClient
    .delete('/api/board-members/remove', {
      params: { boardId, memberId, requesterId },
    })
    .then((res) => res.data);

// --- Lists ---
export const getListsByBoard = (boardId) =>
  apiClient.get(`/api/lists/board/${boardId}`).then((res) => res.data);

export const updateList = (id, data) =>
  apiClient.put(`/api/lists/${id}`, data).then((res) => res.data);

export const deleteList = (id) =>
  apiClient.delete(`/api/lists/${id}`).then((res) => res.data);

export const getLists = () => apiClient.get('/api/lists').then((res) => res.data);

export const createList = (data) =>
  apiClient.post('/api/lists', data).then((res) => res.data);

// --- Cards ---
export const getCardsByListId = (listId) =>
  apiClient.get(`/api/cards/list/${listId}`).then((res) => res.data);

export const getCard = (id) =>
  apiClient.get(`/api/cards/${id}`).then((res) => res.data);

export const updateCard = (id, data) =>
  apiClient.put(`/api/cards/${id}`, data).then((res) => res.data);

export const deleteCard = (id) =>
  apiClient.delete(`/api/cards/${id}`).then((res) => res.data);

export const getCards = () => apiClient.get('/api/cards').then((res) => res.data);

export const createCard = (data) =>
  apiClient.post('/api/cards', data).then((res) => res.data);

// --- Labels ---
export const getLabels = () =>
  apiClient.get('/api/labels').then((res) => res.data);

export const createLabel = (data) =>
  apiClient.post('/api/labels', data).then((res) => res.data);

export const getLabel = (id) =>
  apiClient.get(`/api/labels/${id}`).then((res) => res.data);

export const deleteLabel = (id) =>
  apiClient.delete(`/api/labels/${id}`).then((res) => res.data);

// --- Card Labels ---
export const getCardLabel = (id) =>
  apiClient.get(`/api/cardlabels/${id}`).then((res) => res.data);

export const updateCardLabel = (id, data) =>
  apiClient.put(`/api/cardlabels/${id}`, data).then((res) => res.data);

export const deleteCardLabel = (id) =>
  apiClient.delete(`/api/cardlabels/${id}`).then((res) => res.data);

export const getCardLabels = () =>
  apiClient.get('/api/cardlabels').then((res) => res.data);

export const createCardLabel = (data) =>
  apiClient.post('/api/cardlabels', data).then((res) => res.data);

// --- Checklists ---
export const getChecklist = (checklistId) =>
  apiClient.get(`/api/checklists/${checklistId}`).then((res) => res.data);

export const updateChecklist = (checklistId, data) =>
  apiClient.put(`/api/checklists/${checklistId}`, data).then((res) => res.data);

export const deleteChecklist = (checklistId) =>
  apiClient.delete(`/api/checklists/${checklistId}`).then((res) => res.data);

export const createChecklist = (data) =>
  apiClient.post('/api/checklists', data).then((res) => res.data);

export const getChecklistItems = (checklistId) =>
  apiClient.get(`/api/checklists/${checklistId}/items`).then((res) => res.data);

export const createChecklistItem = (checklistId, data) =>
  apiClient.post(`/api/checklists/${checklistId}/items`, data).then((res) => res.data);

export const updateChecklistPosition = (checklistId, data) =>
  apiClient
    .patch(`/api/checklists/${checklistId}/position`, data)
    .then((res) => res.data);

export const getChecklistItem = (itemId) =>
  apiClient.get(`/api/checklists/items/${itemId}`).then((res) => res.data);

export const updateChecklistItem = (itemId, data) =>
  apiClient.put(`/api/checklists/items/${itemId}`, data).then((res) => res.data);

export const deleteChecklistItem = (itemId) =>
  apiClient.delete(`/api/checklists/items/${itemId}`).then((res) => res.data);

export const toggleChecklistItem = (itemId) =>
  apiClient.patch(`/api/checklists/items/${itemId}/toggle`).then((res) => res.data);

export const updateChecklistItemText = (itemId, data) =>
  apiClient
    .patch(`/api/checklists/items/${itemId}/text`, data)
    .then((res) => res.data);

export const updateChecklistItemPosition = (itemId, data) =>
  apiClient
    .patch(`/api/checklists/items/${itemId}/position`, data)
    .then((res) => res.data);

export const getChecklistProgress = (checklistId) =>
  apiClient.get(`/api/checklists/${checklistId}/progress`).then((res) => res.data);

export const getChecklistDetails = (checklistId) =>
  apiClient.get(`/api/checklists/${checklistId}/details`).then((res) => res.data);

export const getCardChecklists = (cardId) =>
  apiClient.get(`/api/checklists/card/${cardId}`).then((res) => res.data);

export const deleteCompletedChecklistItems = (checklistId) =>
  apiClient.delete(`/api/checklists/${checklistId}/completed-items`).then((res) => res.data);