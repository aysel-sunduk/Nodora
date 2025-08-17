// Bu dosya, projenizdeki 'components' klasörüne yerleştirilmelidir.
// Örneğin: src/components/DeleteConfirmationModal.jsx

import React from 'react';
import './DeleteConfirmationModal.css'; // Yeni CSS dosyasını içe aktarın

/**
 * Kullanıcıdan silme işlemi için onay alan bir modal bileşenidir.
 * @param {object} props - Bileşen özellikleri
 * @param {string} props.message - Modalda gösterilecek onay mesajı
 * @param {function} props.onConfirm - "Sil" butonuna tıklandığında çağrılacak fonksiyon
 * @param {function} props.onCancel - "İptal" butonuna tıklandığında çağrılacak fonksiyon
 * @param {boolean} props.isDarkTheme - Karanlık tema durumunu belirtir
 */
export default function DeleteConfirmationModal({ message, onConfirm, onCancel, isDarkTheme }) {
    // Tema sınıflarını ayarla
    const modalContentClass = `modal-content ${isDarkTheme ? 'dark' : ''}`;
    const confirmButtonClass = `confirm-button ${isDarkTheme ? 'dark' : ''}`;
    const cancelButtonClass = `cancel-button ${isDarkTheme ? 'dark' : ''}`;

    return (
        <div className="modal-backdrop">
            <div className={modalContentClass}>
                <div className="modal-header-icon">
                    {/* Silme ikonu veya uyarı ikonu buraya eklenebilir */}
                    <span className="warning-icon">⚠️</span>
                </div>
                <h4 className="modal-title">Emin misiniz?</h4>
                <p className="modal-message">{message}</p>
                <div className="modal-buttons">
                    <button onClick={onConfirm} className={confirmButtonClass}>
                        Sil
                    </button>
                    <button onClick={onCancel} className={cancelButtonClass}>
                        İptal
                    </button>
                </div>
            </div>
        </div>
    );
}
