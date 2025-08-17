import React, { useState } from 'react';
import { IoClose } from 'react-icons/io5';
import '../components/css/AddBoardMemberPopup.css';

export default function AddBoardMemberPopup({ onClose, onSubmit }) {
    const [email, setEmail] = useState('');
    const [role, setRole] = useState('Üye'); // Varsayılan rol 'Üye'

    const handleSubmit = (e) => {
        e.preventDefault();
        // E-posta adresi boş değilse onSubmit fonksiyonunu çağır
        if (email.trim()) {
            onSubmit({ email, role });
            setEmail('');
            onClose(); // İşlem tamamlandıktan sonra popup'ı kapat
        }
    };

    return (
        <div className="popup-overlay">
            <div className="add-member-popup-content">
                <button className="close-button" onClick={onClose}>
                    <IoClose size={24} />
                </button>
                <h2>Panoya Üye Ekle</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="memberEmail">Üye E-posta Adresi:</label>
                        <input
                            id="memberEmail"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="üye@örnek.com"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="memberRole">Rol Belirle:</label>
                        <select
                            id="memberRole"
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                        >
                            <option value="Üye">Üye</option>
                            <option value="Lider">Lider</option>
                        </select>
                    </div>
                    <div className="form-actions">
                        <button type="button" className="cancel-btn" onClick={onClose}>
                            İptal
                        </button>
                        <button type="submit" className="add-btn">
                            Ekle
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}