// src/components/BoardMembersPage.jsx
import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../components/css/BoardMember.css';
import { getBoardMembers } from '../services/api';

export default function BoardMembersPage() {
    const { workspaceId, boardId } = useParams();
    const navigate = useNavigate();

    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchBoardMembers = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getBoardMembers(boardId);
            if (Array.isArray(data)) {
                setMembers(data);
                console.log(`Pano ${boardId} için üyeler başarıyla çekildi.`, data);
            } else {
                console.error("API'den beklenen formatta veri dönmedi. State boş dizi olarak ayarlanıyor.", data);
                setMembers([]);
            }
        } catch (err) {
            console.error('Üyeler alınırken hata oluştu:', err);
            if (axios.isAxiosError(err)) {
                if (err.response) {
                    const status = err.response.status;
                    const message = err.response.data?.message;

                    // Hata kodlarına göre daha spesifik mesajlar
                    if (status === 403) {
                        setError("Bu panodaki üyeleri görüntüleme yetkiniz yok.");
                    } else if (status === 404) {
                        setError("Pano bulunamadı.");
                    } else {
                        setError(`Sunucu hatası: ${status} - ${message || 'Bilinmeyen Hata'}`);
                    }
                } else if (err.request) {
                    setError("Sunucuya bağlanılamadı. Lütfen internet bağlantınızı ve sunucunuzun açık olduğunu kontrol edin.");
                } else {
                    setError("İstek ayarlanırken bir hata oluştu.");
                }
            } else {
                setError('Beklenmedik bir hata oluştu.');
            }
        } finally {
            setLoading(false);
        }
    }, [boardId]);

    useEffect(() => {
        fetchBoardMembers();
    }, [fetchBoardMembers]);

    const handleReturnToBoard = () => {
        navigate(`/workspace/${workspaceId}/board/${boardId}`);
    };

    return (
        <div className="board-members-page">
            <div className="board-members-header">
                <h1>Pano Üyeleri - Pano {boardId}</h1>
                <button onClick={handleReturnToBoard} className="return-to-board-button">
                    Panoya Geri Dön
                </button>
            </div>
            <div className="board-members-content">
                {loading ? (
                    <p>Üyeler yükleniyor...</p>
                ) : error ? (
                    <p className="error-message">{error}</p>
                ) : (
                    <>
                        <p>Bu panonun üyeleri aşağıda listelenmektedir.</p>
                        <ul className="members-list">
                            {members.length > 0 ? (
                                members.map(item => (
                                    <li key={item.boardMemberId} className="member-item">
                                        <strong>{item.member.name} {item.member.surname}</strong> ({item.member.email}) - {item.role.roleName}
                                    </li>
                                ))
                            ) : (
                                <p>Bu panoda henüz üye bulunmamaktadır.</p>
                            )}
                        </ul>
                    </>
                )}
            </div>
        </div>
    );
}