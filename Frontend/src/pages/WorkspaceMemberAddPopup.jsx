import React, { useState } from 'react';
import { inviteWorkspaceMember } from '../services/api';
import '../components/css/workspace-member-add-popup.css';

export default function WorkspaceMemberAddPopup({ workspaceId, onClose }) {
    // workspaceId prop'unun değerini kontrol etmek için bu satırı ekleyin.
    // Console'da null veya undefined görüyorsanız, sorun burada.
    console.log("Popup'a gelen workspaceId:", workspaceId);

    const [email, setEmail] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!email.trim()) {
            setError('Lütfen geçerli bir email adresi giriniz');
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            const response = await inviteWorkspaceMember({
                workspaceId: workspaceId,
                memberEmail: email
            });

            if (response) {
                onClose();
            }
        } catch (err) {
            console.error('API Hatası:', err);
            setError(err.response?.data?.message || 'Üye davet edilirken bir hata oluştu');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="member-popup-overlay">
            <div className="member-add-popup">
                <h2>Çalışma alanına üye ekle</h2>

                <form onSubmit={handleSubmit}>
                    <div className="member-form-group">
                        <input
                            type="email"
                            className="member-form-input"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Eklemek istediğiniz üyenin emailini giriniz..."
                            required
                        />
                    </div>

                    {error && <div className="member-error-message">{error}</div>}

                    <div className="member-form-actions">
                        <button
                            type="button"
                            className="member-cancel-btn"
                            onClick={onClose}
                            disabled={isLoading}
                        >
                            İptal
                        </button>
                        <button
                            type="submit"
                            className="member-submit-btn"
                            disabled={isLoading}
                        >
                            {isLoading ? 'Çalışma Alanına Üye Ekleniyor...' : 'EKLE !'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
