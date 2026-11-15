import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
    FileText, Smartphone, History, Shield, AlertTriangle,
    CheckCircle2, XCircle, Search, Filter, Trash2, RefreshCw,
    Laptop, Globe, Clock, User, LogOut, ArrowRight
} from 'lucide-react';

export default function AuditPage() {
    const { t } = useTranslation();
    const [activeTab, setActiveTab] = useState('SESSIONS'); // 'SESSIONS' | 'LOGIN_HISTORY' | 'AUDIT_LOGS'
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    // Mock active enterprise sessions with revocation capability
    const [sessions, setSessions] = useState([
        {
            id: 'sess-001',
            username: 'kungsovannda',
            device: 'Chrome on macOS Sonoma',
            ip: '192.168.1.105',
            location: 'Phnom Penh, Cambodia',
            lastActive: 'Just now',
            isCurrent: true,
            icon: Laptop
        },
        {
            id: 'sess-002',
            username: 'ngovkimsouheng-ux',
            device: 'Safari on iPhone 15 Pro',
            ip: '118.107.132.42',
            location: 'Siem Reap, Cambodia',
            lastActive: '12 minutes ago',
            isCurrent: false,
            icon: Smartphone
        },
        {
            id: 'sess-003',
            username: 'alice_developer',
            device: 'Firefox on Ubuntu 24.04',
            ip: '203.189.155.19',
            location: 'Battambang, Cambodia',
            lastActive: '1 hour ago',
            isCurrent: false,
            icon: Laptop
        },
        {
            id: 'sess-004',
            username: 'support_agent',
            device: 'Edge on Windows 11',
            ip: '110.74.214.88',
            location: 'Phnom Penh, Cambodia',
            lastActive: '3 hours ago',
            isCurrent: false,
            icon: Laptop
        }
    ]);

    // Mock global login history
    const [loginHistory, setLoginHistory] = useState([
        {
            id: 'log-1',
            username: 'kungsovannda',
            ip: '192.168.1.105',
            location: 'Phnom Penh, Cambodia',
            browser: 'Chrome 131.0',
            device: 'Desktop (macOS)',
            timestamp: '2026-11-15 02:18:22',
            status: 'SUCCESS'
        },
        {
            id: 'log-2',
            username: 'ngovkimsouheng-ux',
            ip: '118.107.132.42',
            location: 'Siem Reap, Cambodia',
            browser: 'Safari 18.0',
            device: 'Mobile (iOS)',
            timestamp: '2026-11-15 01:45:10',
            status: 'SUCCESS'
        },
        {
            id: 'log-3',
            username: 'attacker_probe',
            ip: '45.133.1.20',
            location: 'Frankfurt, Germany',
            browser: 'curl/7.88.1',
            device: 'Bot / Script',
            timestamp: '2026-11-15 00:32:05',
            status: 'FAILED'
        },
        {
            id: 'log-4',
            username: 'alice_developer',
            ip: '203.189.155.19',
            location: 'Battambang, Cambodia',
            browser: 'Firefox 132.0',
            device: 'Desktop (Linux)',
            timestamp: '2026-11-14 23:14:50',
            status: 'SUCCESS'
        },
        {
            id: 'log-5',
            username: 'unknown_root',
            ip: '185.220.101.5',
            location: 'Tor Exit Node',
            browser: 'HeadlessChrome',
            device: 'Automated Agent',
            timestamp: '2026-11-14 21:02:19',
            status: 'FAILED'
        }
    ]);

    // System audit logs
    const [auditLogs, setAuditLogs] = useState([
        {
            id: 'aud-1',
            actor: 'kungsovannda',
            event: 'USER_ROLE_ASSIGNED',
            details: 'Assigned role ADMIN to user ngovkimsouheng-ux',
            ip: '192.168.1.105',
            timestamp: '2026-11-15 02:22:10',
            level: 'INFO'
        },
        {
            id: 'aud-2',
            actor: 'SYSTEM',
            event: 'CLIENT_INITIALIZATION',
            details: 'Initialized Registered Clients (front-bff-client, oidc-client, customer-client)',
            ip: '127.0.0.1',
            timestamp: '2026-11-15 00:00:01',
            level: 'INFO'
        },
        {
            id: 'aud-3',
            actor: 'kungsovannda',
            event: 'PASSWORD_RESET',
            details: 'Administrative password reset triggered for alice_developer',
            ip: '192.168.1.105',
            timestamp: '2026-11-14 22:50:11',
            level: 'WARN'
        },
        {
            id: 'aud-4',
            actor: 'SYSTEM',
            event: 'SECURITY_LOCKOUT',
            details: 'Temporarily blocked IP 45.133.1.20 after 5 failed attempts',
            ip: '45.133.1.20',
            timestamp: '2026-11-14 20:30:45',
            level: 'SECURITY'
        }
    ]);

    // Session Revocations
    const handleTerminateSession = (sessionId) => {
        setSessions(sessions.filter(s => s.id !== sessionId));
        showToast('Session revoked successfully.');
    };

    const handleTerminateAllSessions = () => {
        setSessions(sessions.filter(s => s.isCurrent));
        showToast('Terminated all remote user sessions.');
    };

    return (
        <div className="space-y-6">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-xl sm:text-2xl font-bold text-slate-800">{t('admin.audit.title')}</h2>
                    <p className="text-sm sm:text-base text-slate-500 mt-1">{t('admin.audit.subtitle')}</p>
                </div>
                {activeTab === 'SESSIONS' && (
                    <button
                        onClick={handleTerminateAllSessions}
                        className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-red-50 hover:bg-red-100 text-[#DF1F28] border border-red-200 font-semibold text-sm transition-all cursor-pointer active:scale-95"
                    >
                        <LogOut size={16} />
                        <span>{t('admin.audit.terminateAllSessions')}</span>
                    </button>
                )}
            </div>

            {/* Tabs */}
            <div className="bg-white p-3 rounded-2xl border border-slate-200 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-2 p-1 bg-slate-100 rounded-xl w-fit">
                    <button
                        onClick={() => setActiveTab('SESSIONS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'SESSIONS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Smartphone size={16} />
                        <span>{t('admin.audit.sessionsTab')} ({sessions.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('LOGIN_HISTORY')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'LOGIN_HISTORY'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <History size={16} />
                        <span>{t('admin.audit.historyTab')} ({loginHistory.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('AUDIT_LOGS')}
                        className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'AUDIT_LOGS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <FileText size={16} />
                        <span>{t('admin.audit.auditTab')} ({auditLogs.length})</span>
                    </button>
                </div>
            </div>

            {/* --- TAB 1: ACTIVE USER SESSIONS --- */}
            {activeTab === 'SESSIONS' && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {sessions.map(sess => {
                        const Icon = sess.icon;
                        return (
                            <div key={sess.id} className="bg-white p-5 rounded-2xl border border-slate-200 shadow-xs flex items-center justify-between gap-4">
                                <div className="flex items-center gap-3.5">
                                    <div className="p-3 rounded-xl bg-[#253C95]/10 text-[#253C95]">
                                        <Icon size={22} />
                                    </div>
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <p className="text-sm sm:text-base font-bold text-slate-800">{sess.username}</p>
                                            {sess.isCurrent && (
                                                <span className="px-2 py-0.5 rounded text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                    Current Admin Session
                                                </span>
                                            )}
                                        </div>
                                        <p className="text-xs sm:text-sm text-slate-500">{sess.device}</p>
                                        <p className="text-xs text-slate-400 mt-1 font-mono">
                                            {sess.ip} &bull; {sess.location}
                                        </p>
                                    </div>
                                </div>
                                {!sess.isCurrent && (
                                    <button
                                        onClick={() => handleTerminateSession(sess.id)}
                                        className="px-3 py-1.5 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-xs sm:text-sm font-semibold transition-all cursor-pointer"
                                    >
                                        {t('admin.audit.terminateSession')}
                                    </button>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}

            {/* --- TAB 2: GLOBAL LOGIN HISTORY --- */}
            {activeTab === 'LOGIN_HISTORY' && (
                <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500">
                                    <th className="px-5 py-4">User</th>
                                    <th className="px-5 py-4">Device / Client</th>
                                    <th className="px-5 py-4">IP & Location</th>
                                    <th className="px-5 py-4">Timestamp</th>
                                    <th className="px-5 py-4 text-right">Status</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 text-sm sm:text-base">
                                {loginHistory.map(log => (
                                    <tr key={log.id} className="hover:bg-slate-50/70 transition-colors">
                                        <td className="px-5 py-4 font-bold text-slate-800 flex items-center gap-2">
                                            <div className="w-8 h-8 rounded-full bg-[#253C95]/10 text-[#253C95] text-xs font-bold flex items-center justify-center">
                                                {log.username.charAt(0).toUpperCase()}
                                            </div>
                                            <span>{log.username}</span>
                                        </td>
                                        <td className="px-5 py-4 text-xs sm:text-sm text-slate-600">
                                            <p className="font-semibold text-slate-700">{log.browser}</p>
                                            <p className="text-slate-400">{log.device}</p>
                                        </td>
                                        <td className="px-5 py-4 text-xs sm:text-sm font-mono text-slate-600">
                                            <p>{log.ip}</p>
                                            <p className="text-slate-400 font-sans text-xs">{log.location}</p>
                                        </td>
                                        <td className="px-5 py-4 text-xs sm:text-sm text-slate-500 font-mono">
                                            {log.timestamp}
                                        </td>
                                        <td className="px-5 py-4 text-right">
                                            {log.status === 'SUCCESS' ? (
                                                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                    <CheckCircle2 size={12} /> Success
                                                </span>
                                            ) : (
                                                <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-red-50 text-[#DF1F28] border border-red-200">
                                                    <XCircle size={12} /> Blocked / Failed
                                                </span>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* --- TAB 3: SYSTEM AUDIT LOGS --- */}
            {activeTab === 'AUDIT_LOGS' && (
                <div className="bg-white rounded-2xl border border-slate-200 shadow-xs overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200 text-xs sm:text-sm font-bold uppercase tracking-wider text-slate-500">
                                    <th className="px-5 py-4">Event Action</th>
                                    <th className="px-5 py-4">Actor</th>
                                    <th className="px-5 py-4">Details</th>
                                    <th className="px-5 py-4">Origin IP</th>
                                    <th className="px-5 py-4 text-right">Timestamp</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 text-sm sm:text-base">
                                {auditLogs.map(item => (
                                    <tr key={item.id} className="hover:bg-slate-50/70 transition-colors">
                                        <td className="px-5 py-4">
                                            <span className={`px-2.5 py-1 rounded-md text-xs font-bold uppercase font-mono ${
                                                item.level === 'SECURITY'
                                                    ? 'bg-red-50 text-[#DF1F28] border border-red-200'
                                                    : item.level === 'WARN'
                                                    ? 'bg-amber-50 text-[#F9A825] border border-amber-200'
                                                    : 'bg-blue-50 text-[#253C95] border border-blue-200'
                                            }`}>
                                                {item.event}
                                            </span>
                                        </td>
                                        <td className="px-5 py-4 font-bold text-slate-800">
                                            {item.actor}
                                        </td>
                                        <td className="px-5 py-4 text-xs sm:text-sm text-slate-600 max-w-md">
                                            {item.details}
                                        </td>
                                        <td className="px-5 py-4 text-xs sm:text-sm font-mono text-slate-500">
                                            {item.ip}
                                        </td>
                                        <td className="px-5 py-4 text-right text-xs sm:text-sm font-mono text-slate-400">
                                            {item.timestamp}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
}

