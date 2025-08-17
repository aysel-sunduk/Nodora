import { useContext, useEffect, useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import { useNavigate, useParams } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { ThemeContext } from '../App';
import '../components/css/workspaceMember.css';
import { getWorkspaceMembers } from '../services/api';

export default function WorkspaceMember() {
    // URL'den hem workspaceId hem de boardId'yi alıyoruz
    // boardId'yi de alıyoruz çünkü geri dönerken panoya ait URL'ye ihtiyacımız var.
    const { workspaceId, boardId } = useParams();
    const navigate = useNavigate();
    const [members, setMembers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const { theme } = useContext(ThemeContext);

    useEffect(() => {
        // İki ID'nin de varlığını kontrol ederek daha sağlam bir yapı kuruyoruz.
        if (!workspaceId || !boardId) {
            setError('Çalışma alanı veya pano ID bulunamadı. Lütfen URL\'yi kontrol edin.');
            setIsLoading(false);
            return;
        }

        const fetchMembers = async () => {
            setIsLoading(true);
            try {
                // API'den üyeleri çekme
                const fetchedMembers = await getWorkspaceMembers(workspaceId);

                if (Array.isArray(fetchedMembers)) {
                    setMembers(fetchedMembers);
                    setError(null);
                } else {
                    console.warn("API'den gelen veri beklenenden farklı bir formatta (dizi değil):", fetchedMembers);
                    setMembers([]);
                    setError("API'den geçersiz üye verisi alındı.");
                }
            } catch (err) {
                console.error("Üyeler yüklenirken hata oluştu:", err);
                setError('Üyeler yüklenirken bir hata oluştu. Lütfen tekrar deneyin.');
            } finally {
                setIsLoading(false);
            }
        };

        fetchMembers();
    }, [workspaceId, boardId]);

    /**
     * Panoya geri dönüş fonksiyonu.
     * Artık doğrudan panonun URL'sine yönlendirme yapıyoruz.
     */
    const handleGoBack = () => {
        if (workspaceId && boardId) {
            // Doğrudan önceki panonun sayfasına yönlendir.
            navigate(`/workspaces/${workspaceId}/board/${boardId}`);
        } else {
            // Eğer ID'ler yoksa, ana çalışma alanları sayfasına dön.
            // Bu, beklenmedik hataları önlemeye yardımcı olur.
            toast.error("Pano kimliği bulunamadı, ana çalışma alanlarına yönlendiriliyor.");
            navigate('/workspaces');
        }
    };

    if (isLoading) {
        return <div className="loading">Yükleniyor...</div>;
    }

    if (error) {
        return (
            <div className="error-message">
                Hata: {error}
                <button onClick={handleGoBack}>Geri Dön</button>
            </div>
        );
    }

    return (
        <div className="workspace-container" data-theme={theme}>
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
                theme={theme === 'dark' ? 'dark' : 'light'}
            />
            <div className="workspace-members-container">
                <div className="member-page-header">
                    <button onClick={handleGoBack} className="back-button">
                        <FiArrowLeft />
                        <span>Panoya Dön</span> {/* Buton metni güncellendi */}
                    </button>
                    <h2 className="page-title">Çalışma Alanı Üyeleri</h2>
                </div>

                <p className="workspace-id">Çalışma Alanı ID: {workspaceId}</p>

                {members.length > 0 ? (
                    <ul className="member-list">
                        {members.map(member => (
                            <li key={member.memberId || Math.random()} className="member-card">
                                <div className="member-info">
                                    <span className="email">{member.email || 'E-posta Bilgisi Yok'}</span>
                                    <span className="name">
                                        {(member.name && member.surname) ? `${member.name} ${member.surname}` : 'İsim Bilgisi Yok'}
                                    </span>
                                </div>
                                <span className={`member-role ${member.roleName ? member.roleName.toLowerCase() : ''}`}>
                                    {member.roleName || 'Bilinmiyor'}
                                </span>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>Bu çalışma alanında henüz üye bulunmamaktadır.</p>
                )}
            </div>
        </div>
    );
}
