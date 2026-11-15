import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../services/userService';
import Toast from '../../components/Toast';
import {
    Link2, Unlink, CheckCircle2, AlertCircle,
    Loader2, Plus, ExternalLink, ShieldCheck, Trash2
} from 'lucide-react';

export default function LinkedAccountsPage() {
    const { t, i18n } = useTranslation();
    const [identities, setIdentities] = useState([]);
    const [loading, setLoading] = useState(true);
    const [unlinkingId, setUnlinkingId] = useState(null);
    const [showUnlinkModal, setShowUnlinkModal] = useState(null);
    const [alert, setAlert] = useState(null);

    useEffect(() => {
        loadIdentities();
    }, []);

    const loadIdentities = async () => {
        try {
            setLoading(true);
            const data = await userService.getUserIdentities();
            setIdentities(data || []);
        } catch (err) {
            console.error(err);
            // Fallback demo identity
            setIdentities([
                {
                    uuid: '1d314d61-5312-421b-8531-102938475612',
                    provider: 'google',
                    email: 'kungsovannda@gmail.com',
                    displayName: 'Google Account',
                    linkedAt: '2026-11-10T12:00:00Z'
                }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const handleUnlinkConfirm = async () => {
        if (!showUnlinkModal) return;
        setUnlinkingId(showUnlinkModal.uuid);
        try {
            await userService.unlinkIdentity(showUnlinkModal.uuid);
            setAlert({
                type: 'success',
                message: i18n.language === 'en'
                    ? `Successfully unlinked ${showUnlinkModal.provider} account.`
                    : `បានផ្តាច់គណនី ${showUnlinkModal.provider} ដោយជោគជ័យ។`
            });
            setShowUnlinkModal(null);
            loadIdentities();
        } catch (err) {
            setAlert({
                type: 'error',
                message: err.response?.data?.detail || (i18n.language === 'en' ? 'Failed to unlink account.' : 'មិនអាចផ្តាច់គណនីបានទេ។')
            });
        } finally {
            setUnlinkingId(null);
        }
    };

    const supportedProviders = [
        {
            id: 'google',
            name: 'Google',
            description: i18n.language === 'en' ? 'Sign in with your Google workspace or personal account' : 'ចូលប្រើប្រាស់ជាមួយគណនី Google',
            icon: (
                <svg className="w-6 h-6" viewBox="0 0 24 24">
                    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" />
                    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
                </svg>
            ),
            authUrl: 'http://localhost:9000/oauth2/authorization/google'
        },
        {
            id: 'github',
            name: 'GitHub',
            description: i18n.language === 'en' ? 'Link your developer profile and repositories' : 'ភ្ជាប់គណនីអ្នកអភិវឌ្ឍន៍ GitHub',
            icon: (
                <svg className="w-6 h-6 fill-current text-slate-900" viewBox="0 0 24 24">
                    <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z" />
                </svg>
            ),
            authUrl: 'http://localhost:9000/oauth2/authorization/github'
        },
        {
            id: 'facebook',
            name: 'Facebook',
            description: i18n.language === 'en' ? 'Connect with your Facebook profile' : 'ភ្ជាប់ជាមួយគណនី Facebook',
            icon: (
                <svg className="w-6 h-6 fill-[#1877F2]" viewBox="0 0 24 24">
                    <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z" />
                </svg>
            ),
            authUrl: 'http://localhost:9000/oauth2/authorization/facebook'
        },
        {
            id: 'telegram',
            name: 'Telegram',
            description: i18n.language === 'en' ? 'Link your Telegram messenger account' : 'ភ្ជាប់គណនី Telegram',
            icon: (
                <svg className="w-6 h-6 fill-[#229ED9]" viewBox="0 0 24 24">
                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.458c.538-.196 1.006.128.832.941z"/>
                </svg>
            ),
            authUrl: 'http://localhost:9000/oauth2/authorization/telegram'
        }
    ];

    return (
        <div className="space-y-5">
            {/* Floating Toast Notification */}
            <Toast alert={alert} onClose={() => setAlert(null)} />

            {/* Header Description */}
            <div className="bg-white p-6 sm:p-7 rounded-3xl border border-slate-200/90 shadow-2xs">
                <div className="flex items-center gap-3 pb-4 border-b border-slate-100 mb-4">
                    <Link2 size={22} className="text-[#253C95]" />
                    <div>
                        <h3 className="text-base sm:text-lg font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Connected Social Accounts' : 'គណនីបណ្តាញសង្គមដែលបានភ្ជាប់'}
                        </h3>
                        <p className="text-xs sm:text-sm text-slate-500 font-medium">
                            {i18n.language === 'en'
                                ? 'Link social accounts to easily sign in with single sign-on.'
                                : 'ភ្ជាប់គណនីបណ្តាញសង្គមដើម្បីចូលប្រើប្រាស់បានលឿន និងមានសុវត្ថិភាព។'}
                        </p>
                    </div>
                </div>

                {loading ? (
                    <div className="flex flex-col items-center justify-center py-10 gap-2">
                        <Loader2 className="animate-spin text-[#253C95]" size={32} />
                        <span className="text-sm text-slate-400">Loading linked accounts...</span>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 gap-4">
                        {supportedProviders.map((prov) => {
                            const linked = identities.find(
                                (id) => id.provider?.toLowerCase() === prov.id.toLowerCase()
                            );

                            return (
                                <div
                                    key={prov.id}
                                    className="p-5 rounded-2xl border border-slate-200/80 hover:border-slate-300 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/40"
                                >
                                    <div className="flex items-center gap-4">
                                        <div className="w-12 h-12 rounded-2xl bg-white border border-slate-200 shadow-2xs flex items-center justify-center shrink-0">
                                            {prov.icon}
                                        </div>
                                        <div>
                                            <div className="flex items-center gap-2">
                                                <h4 className="text-base font-bold text-slate-900">
                                                    {prov.name}
                                                </h4>
                                                {linked && (
                                                    <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                        <ShieldCheck size={14} className="text-emerald-500" />
                                                        {i18n.language === 'en' ? 'Linked' : 'បានភ្ជាប់'}
                                                    </span>
                                                )}
                                            </div>
                                            <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
                                                {linked ? (linked.email || linked.displayName || prov.description) : prov.description}
                                            </p>
                                            {linked?.linkedAt && (
                                                <span className="text-xs text-slate-400 block mt-1 font-mono">
                                                    Linked on: {new Date(linked.linkedAt).toLocaleDateString()}
                                                </span>
                                            )}
                                        </div>
                                    </div>

                                    <div>
                                        {linked ? (
                                            <button
                                                type="button"
                                                onClick={() => setShowUnlinkModal(linked)}
                                                className="px-4 py-2 bg-[#DF1F28]/10 hover:bg-[#DF1F28]/20 text-[#DF1F28] border border-[#DF1F28]/30 font-semibold text-sm rounded-xl transition-all flex items-center gap-2 cursor-pointer active:scale-95"
                                            >
                                                <Unlink size={16} />
                                                <span>{i18n.language === 'en' ? 'Unlink' : 'ផ្តាច់ការភ្ជាប់'}</span>
                                            </button>
                                        ) : (
                                            <a
                                                href={prov.authUrl}
                                                className="px-4 py-2 bg-[#253C95] hover:bg-[#1c2f78] text-white font-semibold text-sm rounded-xl transition-all shadow-sm flex items-center gap-2 active:scale-95"
                                            >
                                                <Plus size={16} />
                                                <span>{i18n.language === 'en' ? 'Connect' : 'ភ្ជាប់គណនី'}</span>
                                            </a>
                                        )}
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {/* Unlink Confirmation Modal */}
            {showUnlinkModal && (
                <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-3xl max-w-md w-full p-6 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95 duration-150">
                        <div className="w-12 h-12 rounded-2xl bg-[#DF1F28]/10 text-[#DF1F28] flex items-center justify-center mb-4">
                            <Trash2 size={24} />
                        </div>
                        <h3 className="text-lg font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Confirm Account Unlinking' : 'បញ្ជាក់ការផ្តាច់គណនី'}
                        </h3>
                        <p className="text-sm text-slate-500 mt-2 leading-relaxed">
                            {i18n.language === 'en'
                                ? `Are you sure you want to unlink your ${showUnlinkModal.provider} account? You will no longer be able to sign in using this provider.`
                                : `តើអ្នកប្រាកដថាចង់ផ្តាច់គណនី ${showUnlinkModal.provider} នេះមែនទេ? អ្នកនឹងមិនអាចចូលប្រើតាមរយៈគណនីនេះទៀតឡើយ។`}
                        </p>

                        <div className="mt-6 flex items-center justify-end gap-3">
                            <button
                                type="button"
                                onClick={() => setShowUnlinkModal(null)}
                                className="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                            >
                                {t('common.cancel')}
                            </button>
                            <button
                                type="button"
                                disabled={unlinkingId === showUnlinkModal.uuid}
                                onClick={handleUnlinkConfirm}
                                className="px-5 py-2 bg-[#DF1F28] hover:bg-[#b8141c] text-white text-sm font-bold rounded-xl shadow-sm transition-all duration-150 active:scale-95 disabled:opacity-50 flex items-center gap-2 cursor-pointer"
                            >
                                {unlinkingId === showUnlinkModal.uuid && <Loader2 size={16} className="animate-spin" />}
                                <span>{i18n.language === 'en' ? 'Unlink' : 'ផ្តាច់'}</span>
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

