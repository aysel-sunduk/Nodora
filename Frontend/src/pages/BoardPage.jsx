import axios from 'axios';
import { useCallback, useEffect, useState } from 'react';
import { FiPlus } from 'react-icons/fi';
import { useNavigate, useParams } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// CSS dosyasını içe aktarıyoruz
import '../components/css/BoardPage.css';

// Bileşenleri içe aktarıyoruz
import AddBoardMemberPopup from './AddBoardMemberPopup.jsx';
import List from './List.jsx';

// API servislerini içe aktarıyoruz
import {
    createCard,
    createList,
    deleteCard,
    deleteList,
    getCardsByListId,
    getListsByBoard
} from '../services/api';

export default function BoardPage() {
    // URL'den hem boardId hem de workspaceId'yi alıyoruz
    const { boardId, workspaceId } = useParams();
    const navigate = useNavigate();

    const [lists, setLists] = useState([]);
    const [showListForm, setShowListForm] = useState(false);
    const [newListTitle, setNewListTitle] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showAddMemberPopup, setShowAddMemberPopup] = useState(false);

    // Koyu tema durumu (İsteğe bağlı, ekrandan gelmediği için false olarak bırakıldı)
    const isDarkTheme = false;

    /**
     * API'den listeleri ve kartları çeken ana fonksiyon.
     */
    const fetchLists = useCallback(async () => {
        if (!boardId) {
            setError("Geçersiz pano ID'si.");
            setLoading(false);
            return;
        }

        console.log(`Pano (${boardId}) için listeler ve kartlar alınıyor...`);
        setLoading(true);
        setError(null);
        try {
            // Adım 1: Listeleri API'den çek
            const listsData = await getListsByBoard(boardId);
            console.log("API'den gelen listeler:", listsData);

            // Adım 2: Her bir liste için kartları çekme işlemini Promise ile yönet
            const listsWithCardsPromises = listsData.map(async (list) => {
                try {
                    const cards = await getCardsByListId(list.listId);
                    console.log(`List ID: ${list.listId} için gelen kartlar:`, cards);
                    return {
                        ...list,
                        cards: cards || [] // Kart verisini listeye ekliyoruz
                    };
                } catch (cardError) {
                    console.error(`List ID ${list.listId} için kartlar çekilirken hata oluştu:`, cardError);
                    return {
                        ...list,
                        cards: [] // Hata durumunda boş bir kart dizisi dön
                    };
                }
            });

            // Adım 3: Tüm kart çekme işlemleri bitince Promise.all ile bekle
            const listsWithCards = await Promise.all(listsWithCardsPromises);
            setLists(listsWithCards);
            console.log("Listeler ve kartlar başarıyla alındı.", listsWithCards);

        } catch (err) {
            console.error("Listeler alınırken hata oluştu:", err);
            if (axios.isAxiosError(err) && err.response) {
                setError(`Sunucu hatası: ${err.response.status} - ${err.response.data?.message || 'Bilinmeyen Hata'}`);
            } else {
                setError("Beklenmedik bir hata oluştu.");
            }
        } finally {
            setLoading(false);
        }
    }, [boardId]);

    /**
     * Bileşen yüklendiğinde ve boardId değiştiğinde listeleri çekiyoruz.
     */
    useEffect(() => {
        fetchLists();
    }, [fetchLists]);

    /**
     * Yeni liste oluşturma fonksiyonu.
     */
    const handleAddList = async () => {
        if (!newListTitle.trim()) {
            toast.warn('Liste başlığı boş olamaz!');
            return;
        }
        try {
            await createList({ boardId, title: newListTitle });
            setNewListTitle('');
            setShowListForm(false);
            await fetchLists();
            toast.success('Liste başarıyla eklendi!');
        } catch (err) {
            console.error("Liste oluşturulurken hata oluştu:", err);
            toast.error("Liste oluşturulurken bir hata oluştu.");
        }
    };

    /**
     * Liste silme fonksiyonu.
     */
    const handleListDelete = async (listId) => {
        try {
            await deleteList(listId);
            await fetchLists();
            toast.success('Liste başarıyla silindi!');
        } catch (err) {
            console.error("Liste silinirken hata oluştu:", err);
            toast.error("Liste silinirken bir hata oluştu.");
        }
    };

    /**
     * Kart ekleme fonksiyonu.
     */
    const handleCardAdd = async (cardData) => {
        try {
            console.log("Kart ekleme işlemi başlatılıyor...");
            const payload = {
                title: cardData.title,
                listId: cardData.listId,
                memberId: 29
            };
            console.log("API'ye gönderilecek veri:", payload);

            await createCard(payload);
            console.log("Kart başarıyla oluşturuldu, listeler yenileniyor...");
            await fetchLists(); // Kart eklendikten sonra listeleri yeniden çekerek güncel veriyi alıyoruz
            toast.success('Kart başarıyla eklendi!');
            console.log("Kart ekleme ve liste yenileme işlemi tamamlandı.");
        } catch (err) {
            console.error("Kart oluşturulurken hata oluştu:", err);
            const errorMessage = axios.isAxiosError(err) && err.response?.data?.message ?
                err.response.data.message :
                'Kart oluşturulurken bir hata oluştu. Lütfen konsolu kontrol edin.';
            toast.error(errorMessage);
        }
    };

    /**
     * Kart güncelleme fonksiyonu.
     */
    const handleCardUpdate = async () => {
        try {
            await fetchLists(); // Güncelleme sonrası listeleri yeniden çekiyoruz
            // toast mesajı List veya Card bileşeninden geleceği için burada eklemiyoruz.
        } catch (err) {
            console.error("Kart güncellenirken hata oluştu:", err);
            // toast mesajı List veya Card bileşeninden geleceği için burada eklemiyoruz.
        }
    };

    /**
     * Kart silme fonksiyonu.
     */
    const handleCardDelete = async (cardId) => {
        try {
            await deleteCard(cardId);
            await fetchLists(); // Silme sonrası listeleri yeniden çekiyoruz
            toast.success('Kart başarıyla silindi!');
        } catch (err) {
            console.error("Kart silinirken hata oluştu:", err);
            toast.error("Kart silinirken bir hata oluştu.");
        }
    };

    // Yeni eklenen yönlendirme fonksiyonu
    const handleViewMembers = () => {
        navigate(`/workspace/${workspaceId}/board/${boardId}/members`);
    };
    
    // Çalışma alanına dönüş için yeni fonksiyon eklendi ve mevcut buton güncellendi
    const handleReturnToWorkspaces = () => {
        // Yönlendirme yapmadan önce workspaceId'yi kontrol et
        console.log("Çalışma alanlarına dönülüyor. workspaceId:", workspaceId);
        // Tüm çalışma alanlarının listelendiği ana sayfaya yönlendirme yapılır.
        navigate('/workspaces');
    };
    
    // Mevcut çalışma alanındaki panolar listesine dönmek için yeni bir fonksiyon eklendi
    const handleReturnToWorkspaceBoards = () => {
        // Yönlendirme yapmadan önce workspaceId'yi kontrol et
        console.log("Çalışma alanı panolarına dönülüyor. workspaceId:", workspaceId);
        if (workspaceId) {
            // Bu, mevcut çalışma alanının panolarını listelediği sayfaya yönlendirecektir.
            navigate(`/workspaces/${workspaceId}`);
        } else {
            console.error("workspaceId bulunamadı. Panolar sayfasına geri dönülemiyor.");
            toast.error("Çalışma alanı kimliği bulunamadı.");
        }
    };

    if (loading) {
        return (
            <div className="board-page">
                <div className="loading-container">
                    <p>Panonuz yükleniyor...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="board-page">
                <div className="error-container">
                    <p style={{ color: 'red' }}>{error}</p>
                    <button onClick={fetchLists}>Tekrar Dene</button>
                </div>
            </div>
        );
    }

    return (
        <div className="board-page">
            <ToastContainer
                position="top-right"
                autoClose={2000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme={isDarkTheme ? 'dark' : 'light'}
            />
            <div className="board-header">
                <h1>Pano {boardId}</h1>
                <div className="board-actions">
                    <button
                        className="view-members-button"
                        onClick={handleViewMembers}
                    >
                        Pano Üyelerini Görüntüle
                    </button>
                    <button
                        className="add-member-button"
                        onClick={() => setShowAddMemberPopup(true)}
                    >
                        Üye Ekle
                    </button>
                    {/* Mevcut çalışma alanına ait panolar listesine dönmek için yeni buton */}
                    <button
                        className="return-button"
                        onClick={handleReturnToWorkspaceBoards}
                    >
                        Çalışma Alanı Panolarına Dön
                    </button>
                    {/* Tüm çalışma alanlarının listelendiği ana sayfaya dönmek için güncellenen buton */}
                    <button
                        className="return-button"
                        onClick={handleReturnToWorkspaces}
                    >
                        Çalışma Alanlarına Dön
                    </button>
                </div>
            </div>

            <div className="lists-container">
                {lists.map((list) => (
                    <List
                        key={list.listId} // list.id yerine list.listId kullanıldı
                        list={list}
                        onListDelete={handleListDelete}
                        onCardAdd={handleCardAdd}
                        onCardUpdate={handleCardUpdate}
                        onCardDelete={handleCardDelete}
                        onUpdate={fetchLists} // Card bileşeninin checklist güncelleme işlemleri için eklendi
                    />
                ))}

                <div className="list-container add-list-container">
                    {showListForm ? (
                        <div className="add-list-form">
                            <input
                                type="text"
                                value={newListTitle}
                                onChange={(e) => setNewListTitle(e.target.value)}
                                placeholder="Liste başlığı girin"
                                autoFocus
                            />
                            <div className="form-actions">
                                <button
                                    className="add-button"
                                    onClick={handleAddList}
                                >
                                    Liste Ekle
                                </button>
                                <button
                                    className="cancel-button"
                                    onClick={() => setShowListForm(false)}
                                >
                                    ×
                                </button>
                            </div>
                        </div>
                    ) : (
                        <button
                            className="add-list-button"
                            onClick={() => setShowListForm(true)}
                        >
                            <FiPlus /> Liste Ekle
                        </button>
                    )}
                </div>
            </div>

            {showAddMemberPopup && (
                <AddBoardMemberPopup
                    onClose={() => setShowAddMemberPopup(false)}
                    boardId={boardId}
                    workspaceId={workspaceId} // workspaceId props olarak gönderildi
                />
            )}
        </div>
    );
}
