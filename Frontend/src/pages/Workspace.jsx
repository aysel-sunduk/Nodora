import { useContext, useEffect, useState } from 'react';
import { FiPlus, FiUsers } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';
import { ThemeContext } from '../App';
import '../components/css/workspace.css';
import { AuthContext } from '../context/AuthContext';
import {
    createBoard,
    createWorkspace,
    deleteWorkspace,
    getBoardsByWorkspace,
    getWorkspacesByMember,
    inviteWorkspaceMember,
} from '../services/api';
import Board from './Board.jsx';
import Settings from './Settings';
import WorkspaceMemberAddPopup from './WorkspaceMemberAddPopup';
import WorkspacePopup from './WorkspacePopup';
import WorkspaceSidebar from './WorkspaceSidebar';

export default function Workspace() {
    const { theme } = useContext(ThemeContext);
    const { user, updateUser } = useContext(AuthContext);

    const memberId = user?.memberId ? Number(user.memberId) : null;
    const roleId = user?.roleId ? Number(user.roleId) : null;
    const roleName = user?.roleName || 'user';

    console.log("AuthContext User:", user);
    console.log("Processed Member ID:", memberId);
    console.log("Processed Role ID:", roleId);
    console.log("Processed Role Name:", roleName);

    const [workspaces, setWorkspaces] = useState([]);
    const [selectedWorkspace, setSelectedWorkspace] = useState(null);
    const [showWorkspacePopup, setShowWorkspacePopup] = useState(false);
    const [showMemberAddPopup, setShowMemberAddPopup] = useState(false);
    const [showAllWorkspaces, setShowAllWorkspaces] = useState(true);
    const [showSettingsDrawer, setShowSettingsDrawer] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (memberId === null || isNaN(memberId)) {
            console.log('User memberId not available or invalid, skipping workspace load.');
            setIsLoading(false);
            if (memberId === null) setError(null);
            return;
        }

        const loadWorkspaces = async () => {
            try {
                setIsLoading(true);
                const fetchedWorkspaces = await getWorkspacesByMember(memberId);
                const mapped = (fetchedWorkspaces || []).map(ws => ({
                    id: ws.workspaceId,
                    name: ws.workspaceName,
                    boards: ws.boards || [],
                    roleId: ws.roleId,
                    roleName: ws.roleName,
                }));
                setWorkspaces(mapped);
                setError(null);
            } catch (err) {
                console.error('Workspace load error:', err);
                setError(
                    err.response?.data?.message ||
                    'Çalışma alanları yüklenirken hata oluştu'
                );
                setWorkspaces([]);
            } finally {
                setIsLoading(false);
            }
        };

        loadWorkspaces();
    }, [memberId]);

    useEffect(() => {
        if (!selectedWorkspace?.id || memberId === null || isNaN(memberId)) return;

        const loadBoards = async () => {
            try {
                const fetchedBoards = await getBoardsByWorkspace(selectedWorkspace.id);
                const mappedBoards = fetchedBoards.map(board => ({
                    id: board.boardId,
                    title: board.title,
                    bgColor: board.bgColor,
                    memberId: board.memberId,
                    workspaceId: board.workspaceId,
                }));
                setSelectedWorkspace(prev => ({ ...prev, boards: mappedBoards }));
            } catch (error) {
                console.error('Boards load error:', error);
            }
        };

        loadBoards();
    }, [selectedWorkspace?.id, memberId]);

    const handleAddWorkspace = async (name) => {
        try {
            if (!name?.trim()) {
                throw new Error('Çalışma alanı adı boş olamaz');
            }
            if (memberId === null || isNaN(memberId)) {
                throw new Error('Kullanıcı bilgisi eksik veya geçersiz');
            }

            const response = await createWorkspace({
                memberId: memberId,
                workspaceName: name.trim(),
            });

            console.log("API'den gelen ham yanıt:", response);

            if (response && response.roleId && response.roleName) {
                const updatedUser = {
                    ...user,
                    roleId: response.roleId,
                    roleName: response.roleName,
                };
                updateUser(updatedUser);
                console.log("AuthContext, yeni kullanıcı verileriyle başarıyla güncellendi:", updatedUser);
            } else {
                console.warn("API yanıtı roleId veya roleName içermiyor. Kullanıcı rolü güncellenemedi.");
            }

            setWorkspaces(prev => [
                ...prev,
                {
                    id: response.workspaceId,
                    name: response.workspaceName,
                    boards: [],
                    roleId: response.roleId,
                    roleName: response.roleName,
                },
            ]);
            setShowWorkspacePopup(false);
        } catch (error) {
            console.error('Workspace oluşturma hatası:', {
                error: error.response?.data || error.message,
                sentData: { memberId: memberId, workspaceName: name },
            });
            setError(error.response?.data?.message || error.message);
        }
    };

    const handleAddBoard = async (boardData) => {
        if (!selectedWorkspace) {
            console.error("Selected workspace is missing for board creation.");
            return;
        }

        if (selectedWorkspace.roleName !== 'OWNER') {
            console.warn("Kullanıcının pano oluşturma yetkisi yok (Role:", selectedWorkspace.roleName, ")");
            return;
        }

        try {
            const newBoardResponse = await createBoard({
                workspaceId: selectedWorkspace.id,
                title: boardData.title,
                bgColor: boardData.bgColor,
            });

            const newBoard = {
                id: newBoardResponse.boardId,
                title: newBoardResponse.title,
                bgColor: newBoardResponse.bgColor,
                memberId: newBoardResponse.memberId,
                workspaceId: newBoardResponse.workspaceId,
            };

            setWorkspaces(prev => prev.map(ws =>
                ws.id === selectedWorkspace.id
                    ? { ...ws, boards: [...(ws.boards || []), newBoard] }
                    : ws
            ));
            setSelectedWorkspace(prev => ({
                ...prev,
                boards: [...(prev.boards || []), newBoard],
            }));
        } catch (error) {
            console.error('Board creation error:', error);
        }
    };

    const handleInviteMember = async (email, role) => {
        try {
            if (!selectedWorkspace) {
                throw new Error('No workspace selected');
            }

            const response = await inviteWorkspaceMember({
                workspaceId: selectedWorkspace.id,
                email,
                role,
            });
            return response;
        } catch (error) {
            console.error('Member invitation error:', error);
            throw error;
        }
    };

    const handleWorkspaceSelect = (workspace) => {
        const updatedUser = {
            ...user,
            roleId: workspace.roleId,
            roleName: workspace.roleName,
        };
        updateUser(updatedUser);

        setSelectedWorkspace(workspace);
        setShowAllWorkspaces(false);
    };

  const handleDeleteWorkspace = async (workspaceId) => {
    // Kullanıcıya bir onay penceresi gösterin
    const isConfirmed = window.confirm(
        'Bu çalışma alanını kalıcı olarak silmek istediğinizden emin misiniz?'
    );

    if (!isConfirmed) {
        return; // Onaylamazsa işlemi durdur
    }

    try {
        await deleteWorkspace(workspaceId);
        // Silme işlemi başarılı olursa, state'i güncelle
        setWorkspaces(prev => prev.filter(ws => ws.id !== workspaceId));
        setSelectedWorkspace(null); // Seçili çalışma alanını sıfırla
        setShowAllWorkspaces(true); // Tüm çalışma alanlarını göster
        alert('Çalışma alanı başarıyla silindi.');
    } catch (error) {
        console.error('Çalışma alanı silme hatası:', error);
        alert('Çalışma alanı silinirken bir hata oluştu.');
    }
};

    const handleOpenSettings = () => setShowSettingsDrawer(true);
    const handleCloseSettings = () => setShowSettingsDrawer(false);

    // Yeni eklenen yönlendirme fonksiyonu
    const handleViewMembers = () => {
        if (selectedWorkspace) {
            navigate(`/workspace/${selectedWorkspace.id}/members`);
        }
    };

    if (isLoading) return <div className='loading'>Yükleniyor...</div>;
    if (error) return <div className='error'>{error}</div>;

    return (
        <div className="workspace-container" data-theme={theme}>
            <WorkspaceSidebar
                workspaces={workspaces} 
                selectedWorkspace={selectedWorkspace}
                onSelectWorkspace={handleWorkspaceSelect}
                onShowAllWorkspaces={() => {
                    setSelectedWorkspace(null);
                    setShowAllWorkspaces(true);
                    if (user && user.memberId) {
                        const defaultUser = { ...user, roleId: user.initialRoleId, roleName: user.initialRoleName };
                        updateUser(defaultUser);
                    }
                }}
                onAddWorkspaceClick={() => setShowWorkspacePopup(true)}
                onDeleteWorkspace={handleDeleteWorkspace}
                onOpenSettings={handleOpenSettings}
                isDarkTheme={theme === 'dark'}
            />

            <main className="workspace-main-content">
                {showAllWorkspaces ? (
                    <div className="workspace-view">
                        <h2>Çalışma Alanlarım</h2>
                        <div className="workspaces-grid">
                            {workspaces.map(workspace => (
                                <div
                                    key={workspace.id}
                                    className="workspace-card"
                                    onClick={() => handleWorkspaceSelect(workspace)}
                                >
                                    <h3>{workspace.name}</h3>
                                    <p>{workspace.boards?.length || 0} Pano</p>
                                </div>
                            ))}
                            <div
                                className="workspace-card add-workspace"
                                onClick={() => setShowWorkspacePopup(true)}
                            >
                                <FiPlus size={24} />
                                <span>Çalışma Alanı Ekle</span>
                            </div>
                        </div>
                    </div>
                ) : selectedWorkspace ? (
                    <div className="board-view">
                        <div className="workspace-header">
                            <h2 className="workspace-name">{selectedWorkspace.name}</h2>
                            <div className="workspace-actions">
                                <button
                                    className="view-members-btn"
                                    onClick={handleViewMembers}
                                >
                                    <FiUsers /> Üyeleri Görüntüle
                                </button>
                                {selectedWorkspace.roleName === 'OWNER' && (
                                    <button
                                        className="add-member-btn"
                                        onClick={() => setShowMemberAddPopup(true)}
                                    >
                                        <FiPlus /> Üye Davet Et
                                    </button>
                                )}
                            </div>
                        </div>
                        {selectedWorkspace.roleName === 'OWNER' ? (
                            <Board
                                boards={selectedWorkspace.boards}
                                workspaceId={selectedWorkspace.id}
                                onCreateBoardSubmit={handleAddBoard}
                            />
                        ) : (
                            <p>Bu çalışma alanında pano oluşturma yetkiniz bulunmamaktadır.</p>
                        )}
                    </div>
                ) : null}
            </main>

            {showWorkspacePopup && (
                <WorkspacePopup
                    onClose={() => setShowWorkspacePopup(false)}
                    onSubmit={handleAddWorkspace}
                />
            )}

            {showMemberAddPopup && selectedWorkspace && (
                <WorkspaceMemberAddPopup
                    onClose={() => setShowMemberAddPopup(false)}
                    onSubmit={handleInviteMember}
                    workspaceId={selectedWorkspace.id}
                />
            )}

            <Settings open={showSettingsDrawer} onClose={handleCloseSettings} />
        </div>
    );
}