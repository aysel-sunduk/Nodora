import { useState } from 'react';
import { FiCheck, FiPlus, FiTrash2 } from 'react-icons/fi';
import { toast } from 'react-toastify';
import '../components/css/Card.css'; // CSS yolu muhtemelen değişmedi
import DeleteConfirmationModal from '../components/DeleteConfirmationModal'; // Modal yolunu güncelledik
import { createChecklist, createChecklistItem, toggleChecklistItem } from '../services/api';

export default function Card({ card, onUpdate, onDelete, isDarkTheme }) {
    const [newChecklistItem, setNewChecklistItem] = useState('');
    const [showChecklistForm, setShowChecklistForm] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false); // Modal durumunu yönetmek için yeni state

    /**
     * Yeni bir checklist öğesi ekler.
     */
    const handleAddChecklist = async () => {
        if (!newChecklistItem.trim()) {
            toast.warn('Checklist öğesi boş olamaz!');
            return;
        }

        try {
            let checklistId;

            if (card.checklists && card.checklists.length > 0) {
                checklistId = card.checklists[0].checklistId;
            } else {
                const newChecklist = await createChecklist({ cardId: card.cardId, title: 'Checklist' });
                checklistId = newChecklist.checklistId;
            }

            await createChecklistItem(checklistId, {
                text: newChecklistItem,
                isCompleted: false
            });

            onUpdate();
            setNewChecklistItem('');
            setShowChecklistForm(false);
            toast.success('Checklist öğesi eklendi!');
        } catch (error) {
            console.error('Checklist öğesi eklenirken hata oluştu:', error);
            toast.error('Checklist öğesi eklenirken bir hata oluştu.');
        }
    };

    /**
     * Checklist öğesinin tamamlanma durumunu değiştirir.
     */
    const toggleChecklist = async (itemId) => {
        try {
            await toggleChecklistItem(itemId);
            onUpdate();
            toast.success('Checklist öğesi güncellendi!');
        } catch (error) {
            console.error('Checklist durumu güncellenirken hata oluştu:', error);
            toast.error('Checklist durumu güncellenirken bir hata oluştu.');
        }
    };

    /**
     * Modal üzerinden onaylandıktan sonra kartı silme işlemini gerçekleştirir.
     */
    const confirmAndDeleteCard = async () => {
        try {
            await onDelete(card.cardId);
            toast.success('Kart başarıyla silindi!');
        } catch (error) {
            console.error('Kart silinirken hata oluştu:', error);
            toast.error('Kart silinirken bir hata oluştu.');
        } finally {
            setShowDeleteModal(false); // İşlem bitince veya hata olsa bile modalı kapat
        }
    };

    return (
        <div className="card-container">
            {showDeleteModal && (
                <DeleteConfirmationModal
                    message={`"${card.title}" adlı kartı silmek istediğinizden emin misiniz?`}
                    onConfirm={confirmAndDeleteCard}
                    onCancel={() => setShowDeleteModal(false)}
                    isDarkTheme={isDarkTheme}
                />
            )}

            <div className="card-header">
                <h4 className="card-title">{card.title}</h4>
                <button
                    className="delete-card-button"
                    onClick={() => setShowDeleteModal(true)} // Tıklanınca modalı aç
                    aria-label="Kartı sil"
                >
                    <FiTrash2 size={14} />
                </button>
            </div>
            
            {/* ... (Geri kalan kısım aynı kalabilir) ... */}
            
            {card.checklists && card.checklists.length > 0 && (
                <div className="checklists-container">
                    {card.checklists.map((checklist) => (
                        <div key={checklist.checklistId}>
                            <h5>{checklist.title}</h5>
                            {checklist.items && checklist.items.length > 0 && (
                                <>
                                    {checklist.items.map((item) => (
                                        <div key={item.checklistItemsId} className="checklist-item">
                                            <button
                                                className={`check-box ${item.isCompleted ? 'checked' : ''}`}
                                                onClick={() => toggleChecklist(item.checklistItemsId)}
                                            >
                                                {item.isCompleted && <FiCheck size={10} />}
                                            </button>
                                            <span className={item.isCompleted ? 'completed' : ''}>
                                                {item.text}
                                            </span>
                                        </div>
                                    ))}
                                </>
                            )}
                        </div>
                    ))}
                </div>
            )}

            {showChecklistForm ? (
                <div className="add-checklist-form">
                    <input
                        type="text"
                        value={newChecklistItem}
                        onChange={(e) => setNewChecklistItem(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                handleAddChecklist();
                            }
                        }}
                        placeholder="Checklist öğesi ekle"
                        autoFocus
                    />
                    <div className="form-actions">
                        <button
                            className="add-button"
                            onClick={handleAddChecklist}
                        >
                            Ekle
                        </button>
                        <button
                            className="cancel-button"
                            onClick={() => setShowChecklistForm(false)}
                        >
                            ×
                        </button>
                    </div>
                </div>
            ) : (
                <button
                    className="add-checklist-button"
                    onClick={() => setShowChecklistForm(true)}
                >
                    <FiPlus size={12} /> Öğe Ekle
                </button>
            )}
        </div>
    );
}