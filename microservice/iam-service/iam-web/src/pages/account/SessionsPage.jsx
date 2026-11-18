import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { userService } from '../../services/userService';
import Toast from '../../components/Toast';
import {
    Smartphone, Monitor, Tablet, Globe, Clock,
    LogOut, CheckCircle2, AlertCircle, Shield,
    Laptop, Trash2, RefreshCw
} from 'lucide-react';

export default function SessionsPage() {
    const { t, i18n } = useTranslation();
    const [alert, setAlert] = useState(null);
    const [loading, setLoading] = useState(true);

    const [sessions, setSessions] = useState([]);

    const fetchSessions = async () => {
        setLoading(true);
        try {
            const data = await userService.getActiveSessions();
            if (Array.isArray(data)) {
                const formatted = data.map(s => {
                    const dev = (s.device || '').toLowerCase();
                    let deviceType = 'desktop';
                    if (dev.includes('mobile') || dev.includes('iphone') || dev.includes('android')) deviceType = 'mobile';
                    else if (dev.includes('mac') || dev.includes('laptop')) deviceType = 'laptop';

                    return {
                        id: s.sessionId,
                        deviceType,
                        deviceName: s.device || 'Web Browser',
                        browser: dev.includes('chrome') ? 'Chrome' : dev.includes('safari') ? 'Safari' : dev.includes('firefox') ? 'Firefox' : 'Browser',
                        ipAddress: s.ipAddress || '127.0.0.1 (Localhost)',
                        location: 'Phnom Penh, Cambodia',
                        lastActive: s.lastAccessedAt ? new Date(s.lastAccessedAt).toLocaleTimeString() : 'Active now',
                        isCurrent: Boolean(s.isCurrent),
                        createdAt: s.createdAt
                    };
                });
                setSessions(formatted);
            }
        } catch (err) {
            console.error('Could not fetch sessions from backend:', err);
            setAlert({
                type: 'error',
                message: 'Failed to load active sessions from server.'
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSessions();
    }, []);

    const handleTerminate = async (id, name) => {
        try {
            await userService.terminateSession(id);
            setSessions(prev => prev.filter((s) => s.id !== id));
            setAlert({
                type: 'success',
                message: i18n.language === 'en'
                    ? `Successfully terminated session for ${name}.`
                    : `បានបញ្ចប់សម័យការងារសម្រាប់ ${name} ដោយជោគជ័យ។`
            });
        } catch (e) {
            console.error('Failed to terminate session:', e);
            setAlert({
                type: 'error',
                message: e.response?.data?.detail || 'Failed to terminate session on server.'
            });
        }
    };

    const handleTerminateAllOthers = async () => {
        try {
            await userService.terminateAllOtherSessions();
            setSessions(prev => prev.filter((s) => s.isCurrent));
            setAlert({
                type: 'success',
                message: i18n.language === 'en'
                    ? 'All other active sessions have been successfully terminated.'
                    : 'បានបញ្ចប់សម័យការងារលើឧបករណ៍ផ្សេងទៀតទាំងអស់ដោយជោគជ័យ។'
            });
        } catch (e) {
            console.error('Failed to terminate all sessions:', e);
            setAlert({
                type: 'error',
                message: e.response?.data?.detail || 'Failed to terminate other sessions.'
            });
        }
    };

    const getDeviceIcon = (type) => {
        switch (type) {
            case 'mobile': return <Smartphone size={22} className="text-[#253C95]" />;
            case 'tablet': return <Tablet size={22} className="text-[#253C95]" />;
            case 'laptop': return <Laptop size={22} className="text-[#253C95]" />;
            default: return <Monitor size={22} className="text-[#253C95]" />;
        }
    };

    return (
        <div className="space-y-4">
            {/* Floating Toast Notification */}
            <Toast alert={alert} onClose={() => setAlert(null)} />

            {/* Header & Terminate All Actions */}
            <div className="bg-white px-5 py-4 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
                <div>
                    <div className="flex items-center gap-2">
                        <Smartphone size={20} className="text-[#253C95]" />
                        <h3 className="text-sm sm:text-base font-bold text-slate-900">
                            {i18n.language === 'en' ? 'Active User Sessions' : 'សម័យការងារសកម្ម'}
                        </h3>
                    </div>
                    <p className="text-xs text-slate-500 font-medium mt-0.5">
                        {i18n.language === 'en'
                            ? 'Manage devices where your account is currently signed in via Redis session store.'
                            : 'គ្រប់គ្រងឧបករណ៍ដែលកំពុងចូលប្រើប្រាស់គណនីរបស់អ្នកតាមរយៈឃ្លាំងផ្ទុកទិន្នន័យ Redis។'}
                    </p>
                </div>

                <div className="flex items-center gap-2">
                    <button
                        onClick={fetchSessions}
                        disabled={loading}
                        className="p-2 rounded-xl border border-slate-200 hover:bg-slate-50 text-slate-600 transition-all cursor-pointer active:scale-95"
                        title="Reload Sessions"
                    >
                        <RefreshCw size={15} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>

                    {sessions.length > 1 && (
                        <button
                            type="button"
                            onClick={handleTerminateAllOthers}
                            className="px-3 py-2 bg-[#DF1F28]/10 hover:bg-[#DF1F28]/20 text-[#DF1F28] border border-[#DF1F28]/30 font-semibold text-xs sm:text-sm rounded-xl transition-all flex items-center gap-1.5 cursor-pointer active:scale-95 shrink-0"
                        >
                            <Trash2 size={14} />
                            <span>{i18n.language === 'en' ? 'Log Out Others' : 'ចាកចេញពីឧបករណ៍ផ្សេងទៀត'}</span>
                        </button>
                    )}
                </div>
            </div>

            {/* Sessions List */}
            <div className="space-y-3">
                {sessions.map((sess) => (
                    <div
                        key={sess.id}
                        className={`bg-white px-4 py-3 sm:px-5 sm:py-3.5 rounded-2xl border transition-all flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 shadow-2xs ${
                            sess.isCurrent ? 'border-[#253C95]/40 bg-[#253C95]/[0.02]' : 'border-slate-200/80 hover:border-slate-300'
                        }`}
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-[#253C95]/10 border border-[#253C95]/20 flex items-center justify-center shrink-0">
                                {getDeviceIcon(sess.deviceType)}
                            </div>

                            <div>
                                <div className="flex flex-wrap items-center gap-2">
                                    <h4 className="text-xs sm:text-sm font-bold text-slate-900 leading-tight">
                                        {sess.deviceName}
                                    </h4>
                                    <span className="text-xs text-slate-500 font-medium">
                                        • {sess.browser}
                                    </span>
                                    {sess.isCurrent && (
                                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                            <span className="w-1.5 h-1.5 rounded-full bg-[#F9A825]" />
                                            {i18n.language === 'en' ? 'Current Session' : 'សម័យការងារបច្ចុប្បន្ន'}
                                        </span>
                                    )}
                                </div>

                                <div className="flex flex-wrap items-center gap-2.5 mt-1 text-xs text-slate-500">
                                    <div className="flex items-center gap-1 font-mono text-xs">
                                        <Globe size={13} className="text-slate-400" />
                                        <span>{sess.ipAddress}</span>
                                    </div>
                                    <span className="text-slate-300">•</span>
                                    <span className="text-xs">{sess.location}</span>
                                    <span className="text-slate-300">•</span>
                                    <div className="flex items-center gap-1 text-xs">
                                        <Clock size={13} className="text-slate-400" />
                                        <span>{sess.lastActive}</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {!sess.isCurrent && (
                            <button
                                type="button"
                                onClick={() => handleTerminate(sess.id, sess.deviceName)}
                                className="px-3 py-1.5 bg-slate-50 hover:bg-[#DF1F28]/10 text-slate-600 hover:text-[#DF1F28] border border-slate-200 hover:border-[#DF1F28]/30 font-semibold text-xs rounded-xl transition-all flex items-center gap-1.5 cursor-pointer active:scale-95 shrink-0 ml-auto sm:ml-0"
                            >
                                <LogOut size={14} />
                                <span>{i18n.language === 'en' ? 'Terminate' : 'បញ្ចប់'}</span>
                            </button>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}
