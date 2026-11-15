import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { adminService } from '../../services/adminService';
import {
    FileText, Smartphone, History, Shield, AlertTriangle,
    CheckCircle2, XCircle, Search, Filter, Trash2, RefreshCw,
    Laptop, Globe, Clock, User, LogOut, ArrowRight, Loader2,
    Monitor, Tablet
} from 'lucide-react';

export default function AuditPage() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('AUDIT_LOGS'); // 'AUDIT_LOGS' | 'LOGIN_HISTORY' | 'SESSIONS'
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [loading, setLoading] = useState(true);

    const [sessions, setSessions] = useState([]);
    const [loginHistory, setLoginHistory] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);

    // Toast
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const showToast = (message, type = 'success') => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 3500);
    };

    const fetchAuditLogs = async () => {
        setLoading(true);
        try {
            const data = await adminService.getEnterpriseSecurityActivity();
            setAuditLogs(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise security activity:', err);
            setAuditLogs([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchLoginHistory = async (status = 'ALL') => {
        setLoading(true);
        try {
            const data = await adminService.getEnterpriseLoginHistory(status);
            setLoginHistory(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise login history:', err);
            setLoginHistory([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchSessions = async () => {
        setLoading(true);
        try {
            const data = await adminService.getEnterpriseSessions();
            setSessions(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to load enterprise sessions:', err);
            setSessions([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (activeTab === 'AUDIT_LOGS') {
            fetchAuditLogs();
        } else if (activeTab === 'LOGIN_HISTORY') {
            fetchLoginHistory(statusFilter);
        } else if (activeTab === 'SESSIONS') {
            fetchSessions();
        }
    }, [activeTab, statusFilter]);

    // Terminate individual session
    const handleTerminateSession = async (sessionId) => {
        try {
            await adminService.terminateEnterpriseSession(sessionId);
            setSessions(prev => prev.filter(s => s.sessionId !== sessionId));
            showToast('Session terminated successfully.');
        } catch (err) {
            showToast('Failed to terminate session.', 'error');
        }
    };

    const getDeviceIcon = (deviceStr = '') => {
        const d = deviceStr.toLowerCase();
        if (d.includes('mobile') || d.includes('iphone') || d.includes('android')) return Smartphone;
        if (d.includes('tablet') || d.includes('ipad')) return Tablet;
        if (d.includes('mac') || d.includes('laptop')) return Laptop;
        return Monitor;
    };

    const formatTimestamp = (ts) => {
        if (!ts) return '-';
        try {
            return new Date(ts).toLocaleString();
        } catch {
            return String(ts);
        }
    };

    // Filtered lists
    const filteredAuditLogs = auditLogs.filter(log => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.description?.toLowerCase().includes(q)) ||
               (log.details?.toLowerCase().includes(q)) ||
               (log.username?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q));
    });

    const filteredLoginHistory = loginHistory.filter(log => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (log.device?.toLowerCase().includes(q)) ||
               (log.ipAddress?.toLowerCase().includes(q)) ||
               (log.location?.toLowerCase().includes(q)) ||
               (log.authMethod?.toLowerCase().includes(q));
    });

    const filteredSessions = sessions.filter(sess => {
        if (!searchQuery.trim()) return true;
        const q = searchQuery.toLowerCase();
        return (sess.username?.toLowerCase().includes(q)) ||
               (sess.device?.toLowerCase().includes(q)) ||
               (sess.ipAddress?.toLowerCase().includes(q));
    });

    return (
        <div className="space-y-5">
            {/* Toast Notification */}
            {toast.show && (
                <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-xl text-white font-medium text-sm transition-all duration-300 ${
                    toast.type === 'error' ? 'bg-[#DF1F28]' : 'bg-emerald-600'
                }`}>
                    {toast.type === 'error' ? <AlertTriangle size={18} /> : <CheckCircle2 size={18} />}
                    <span>{toast.message}</span>
                </div>
            )}

            {/* Header */}
            <div className="bg-white p-5 sm:p-6 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-lg sm:text-xl font-bold text-slate-900">
                        {i18n.language === 'en' ? 'Audit & Security Compliance' : 'ការត្រួតពិនិត្យ និងសុវត្ថិភាព'}
                    </h2>
                    <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
                        {i18n.language === 'en'
                            ? 'Real-time enterprise audit trail, sign-in attempts, and active user sessions.'
                            : 'កំណត់ត្រាសុវត្ថិភាពសហគ្រាស ការចូលប្រើប្រាស់ និងសម័យការងារសកម្ម។'}
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => {
                            if (activeTab === 'AUDIT_LOGS') fetchAuditLogs();
                            else if (activeTab === 'LOGIN_HISTORY') fetchLoginHistory(statusFilter);
                            else fetchSessions();
                        }}
                        className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition-all cursor-pointer"
                        title="Refresh"
                    >
                        <RefreshCw size={17} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                    </button>
                </div>
            </div>

            {/* Tabs & Search Controls */}
            <div className="bg-white p-3 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="flex items-center gap-1.5 p-1 bg-slate-100 rounded-xl w-fit flex-wrap">
                    <button
                        onClick={() => setActiveTab('AUDIT_LOGS')}
                        className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'AUDIT_LOGS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <FileText size={15} />
                        <span>{i18n.language === 'en' ? 'Audit Events' : 'កំណត់ហេតុសកម្មភាព'} ({auditLogs.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('LOGIN_HISTORY')}
                        className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'LOGIN_HISTORY'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <History size={15} />
                        <span>{i18n.language === 'en' ? 'Sign-in Attempts' : 'ប្រវត្តិចូលប្រើ'} ({loginHistory.length})</span>
                    </button>
                    <button
                        onClick={() => setActiveTab('SESSIONS')}
                        className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                            activeTab === 'SESSIONS'
                                ? 'bg-white text-[#253C95] shadow-xs'
                                : 'text-slate-600 hover:text-slate-900'
                        }`}
                    >
                        <Smartphone size={15} />
                        <span>{i18n.language === 'en' ? 'Active Sessions' : 'សម័យការងារសកម្ម'} ({sessions.length})</span>
                    </button>
                </div>

                <div className="flex items-center gap-2">
                    <div className="relative flex-1 sm:w-64">
                        <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" />
                        <input
                            type="text"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            placeholder={i18n.language === 'en' ? 'Filter events, users, IP...' : 'ស្វែងរកតាមព្រឹត្តិការណ៍ អ្នកប្រើ ឬ IP...'}
                            className="w-full pl-9 pr-3 py-1.5 rounded-xl border border-slate-200 text-xs sm:text-sm outline-none focus:border-[#253C95] bg-slate-50/50 hover:bg-white focus:bg-white"
                        />
                    </div>
                </div>
            </div>

            {/* Loading State */}
            {loading ? (
                <div className="bg-white p-12 rounded-2xl border border-slate-200/90 shadow-2xs flex flex-col items-center justify-center gap-2 text-slate-400">
                    <Loader2 size={32} className="animate-spin text-[#253C95]" />
                    <span className="text-xs font-medium">Loading audit data...</span>
                </div>
            ) : (
                <>
                    {/* --- TAB 1: SYSTEM AUDIT LOGS --- */}
                    {activeTab === 'AUDIT_LOGS' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredAuditLogs.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-xs sm:text-sm font-medium">
                                    No audit events recorded yet.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-xs sm:text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-xs font-bold uppercase tracking-wider text-slate-500">
                                                <th className="px-5 py-3">Event Action</th>
                                                <th className="px-5 py-3">Details / User</th>
                                                <th className="px-5 py-3">Origin IP</th>
                                                <th className="px-5 py-3 text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredAuditLogs.map(item => (
                                                <tr key={item.id} className="hover:bg-slate-50/60 transition-colors">
                                                    <td className="px-5 py-3 whitespace-nowrap">
                                                        <span className="inline-block px-2.5 py-1 rounded-md text-xs font-bold uppercase font-mono bg-blue-50 text-[#253C95] border border-blue-200/60">
                                                            {item.description || item.category}
                                                        </span>
                                                    </td>
                                                    <td className="px-5 py-3 text-slate-700">
                                                        <p className="font-semibold text-slate-900 leading-tight">
                                                            {item.details || item.description}
                                                        </p>
                                                    </td>
                                                    <td className="px-5 py-3 font-mono text-slate-600 text-xs">
                                                        {item.ipAddress}
                                                    </td>
                                                    <td className="px-5 py-3 text-right text-xs font-mono text-slate-400 whitespace-nowrap">
                                                        {formatTimestamp(item.timestamp)}
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 2: GLOBAL LOGIN HISTORY --- */}
                    {activeTab === 'LOGIN_HISTORY' && (
                        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                            {filteredLoginHistory.length === 0 ? (
                                <div className="p-10 text-center text-slate-400 text-xs sm:text-sm font-medium">
                                    No login attempts found.
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left text-xs sm:text-sm border-collapse">
                                        <thead>
                                            <tr className="bg-slate-50/80 border-b border-slate-100 text-xs font-bold uppercase tracking-wider text-slate-500">
                                                <th className="px-5 py-3">Status</th>
                                                <th className="px-5 py-3">Device & Method</th>
                                                <th className="px-5 py-3">IP & Location</th>
                                                <th className="px-5 py-3 text-right">Timestamp</th>
                                            </tr>
                                        </thead>
                                        <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                            {filteredLoginHistory.map(log => (
                                                <tr key={log.id} className="hover:bg-slate-50/60 transition-colors">
                                                    <td className="px-5 py-3 whitespace-nowrap">
                                                        {log.status === 'SUCCESS' ? (
                                                            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                <CheckCircle2 size={12} className="text-emerald-500" /> Success
                                                            </span>
                                                        ) : (
                                                            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-50 text-[#DF1F28] border border-red-200">
                                                                <XCircle size={12} className="text-[#DF1F28]" /> Failed
                                                            </span>
                                                        )}
                                                    </td>
                                                    <td className="px-5 py-3">
                                                        <p className="font-semibold text-slate-900 leading-tight">{log.device}</p>
                                                        <span className="inline-block mt-0.5 px-2 py-0.5 rounded text-xs font-bold uppercase bg-slate-100 text-slate-600">
                                                            {log.authMethod || 'Password'}
                                                        </span>
                                                    </td>
                                                    <td className="px-5 py-3">
                                                        <p className="font-mono text-xs text-slate-700 font-medium">{log.ipAddress}</p>
                                                        <p className="text-xs text-slate-400 mt-0.5">{log.location}</p>
                                                    </td>
                                                    <td className="px-5 py-3 text-right text-xs font-mono text-slate-500 whitespace-nowrap">
                                                        {formatTimestamp(log.timestamp)}
                                                    </td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    )}

                    {/* --- TAB 3: ACTIVE ENTERPRISE SESSIONS --- */}
                    {activeTab === 'SESSIONS' && (
                        <div>
                            {filteredSessions.length === 0 ? (
                                <div className="bg-white p-10 rounded-2xl border border-slate-200/90 text-center text-slate-400 text-xs sm:text-sm font-medium">
                                    No active user sessions currently stored in Redis.
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
                                    {filteredSessions.map(sess => {
                                        const DeviceIcon = getDeviceIcon(sess.device);
                                        return (
                                            <div key={sess.sessionId} className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200/90 shadow-2xs flex items-center justify-between gap-4">
                                                <div className="flex items-center gap-3.5 min-w-0">
                                                    <div className="w-10 h-10 rounded-xl bg-[#253C95]/10 text-[#253C95] flex items-center justify-center shrink-0">
                                                        <DeviceIcon size={20} />
                                                    </div>
                                                    <div className="min-w-0">
                                                        <div className="flex items-center gap-2">
                                                            <p className="text-sm font-bold text-slate-900 truncate">
                                                                @{sess.username}
                                                            </p>
                                                            {sess.isCurrent && (
                                                                <span className="px-2 py-0.5 rounded text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                                    Your Session
                                                                </span>
                                                            )}
                                                        </div>
                                                        <p className="text-xs text-slate-500 mt-0.5 truncate">{sess.device}</p>
                                                        <p className="text-xs text-slate-400 font-mono mt-0.5">
                                                            {sess.ipAddress} &bull; {sess.lastAccessedAt ? formatTimestamp(sess.lastAccessedAt) : 'Active now'}
                                                        </p>
                                                    </div>
                                                </div>
                                                {!sess.isCurrent && (
                                                    <button
                                                        onClick={() => handleTerminateSession(sess.sessionId)}
                                                        className="px-3 py-1.5 rounded-xl border border-red-200 text-[#DF1F28] hover:bg-red-50 text-xs font-semibold transition-all cursor-pointer shrink-0"
                                                    >
                                                        Terminate
                                                    </button>
                                                )}
                                            </div>
                                        );
                                    })}
                                </div>
                            )}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
