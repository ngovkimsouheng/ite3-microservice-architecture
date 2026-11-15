import React, { useEffect, useRef, useState } from 'react';
import { X, Loader2, Send } from 'lucide-react';
import api from '../api/axios';

export default function TelegramLoginModal({ isOpen, onClose, mode = 'login' }) {
    const [botUsername, setBotUsername] = useState('');
    const [loading, setLoading] = useState(true);
    const containerRef = useRef(null);

    useEffect(() => {
        if (!isOpen) return;

        let isMounted = true;
        setLoading(true);

        const init = async () => {
            try {
                if (mode === 'link') {
                    // Prepare session for linking
                    await api.get('/auth/telegram/prepare-link');
                }

                // Fetch configured bot username
                const res = await api.get('/auth/telegram/config');
                if (isMounted) {
                    const username = res.data?.botUsername || '';
                    setBotUsername(username);
                }
            } catch (err) {
                console.error('Failed to prepare Telegram auth:', err);
            } finally {
                if (isMounted) setLoading(false);
            }
        };

        init();

        return () => {
            isMounted = false;
        };
    }, [isOpen, mode]);

    useEffect(() => {
        if (!isOpen || loading || !botUsername || !containerRef.current) return;

        // Clear existing children
        containerRef.current.innerHTML = '';

        // Build callback URL pointing to the active origin (e.g. ngrok HTTPS or localhost)
        const callbackUrl = `${window.location.origin}/api/auth/telegram/callback`;

        // Create official Telegram script widget
        const script = document.createElement('script');
        script.src = 'https://telegram.org/js/telegram-widget.js?22';
        script.setAttribute('data-telegram-login', botUsername);
        script.setAttribute('data-size', 'large');
        script.setAttribute('data-radius', '14');
        script.setAttribute('data-auth-url', callbackUrl);
        script.setAttribute('data-request-access', 'write');
        script.async = true;

        containerRef.current.appendChild(script);
    }, [isOpen, loading, botUsername]);

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-3xl max-w-sm w-full p-6 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95 duration-150 relative">
                {/* Close Button */}
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-slate-400 hover:text-slate-600 p-1.5 rounded-xl hover:bg-slate-100 transition-all cursor-pointer"
                >
                    <X size={18} />
                </button>

                {/* Header */}
                <div className="flex flex-col items-center text-center mb-5">
                    <div className="w-14 h-14 rounded-2xl bg-[#229ED9]/10 text-[#229ED9] flex items-center justify-center mb-3 shadow-md shadow-[#229ED9]/10">
                        <Send size={28} className="translate-x-0.5 -translate-y-0.5" />
                    </div>
                    <h3 className="text-lg font-bold text-slate-900">
                        {mode === 'link' ? 'Link Telegram Account' : 'Sign in with Telegram'}
                    </h3>
                    <p className="text-xs sm:text-sm text-slate-500 mt-1">
                        Authenticate securely via your Telegram app
                    </p>
                </div>

                {/* Content */}
                <div className="min-h-[70px] flex items-center justify-center">
                    {loading ? (
                        <div className="flex items-center gap-2 text-slate-400 text-sm">
                            <Loader2 className="animate-spin text-[#229ED9]" size={20} />
                            <span>Loading Telegram widget...</span>
                        </div>
                    ) : !botUsername ? (
                        <div className="text-center p-3 bg-amber-50 rounded-2xl border border-amber-200 text-amber-800 text-xs font-medium">
                            <p className="font-bold mb-1">Bot Username Not Configured</p>
                            <p>Please set <code>TELEGRAM_BOT_USERNAME</code> in your configuration.</p>
                        </div>
                    ) : (
                        <div ref={containerRef} className="flex justify-center" />
                    )}
                </div>

                <div className="mt-5 text-center">
                    <button
                        type="button"
                        onClick={onClose}
                        className="w-full py-2.5 px-4 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                    >
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    );
}
