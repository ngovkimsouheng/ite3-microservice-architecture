import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import {
    History, ShieldAlert, CheckCircle2, XCircle,
    Clock, Globe, Monitor, Smartphone, Filter,
    KeyRound, UserCheck, Lock, Activity, Loader2, RefreshCw,
    MapPin
} from 'lucide-react';
import CustomSelect from '../../components/CustomSelect';
import { userService } from '../../services/userService';

const ICON_MAP = {
    KeyRound: KeyRound,
    UserCheck: UserCheck,
    Lock: Lock,
    Activity: Activity
};

const formatIp = (ip) => {
    if (!ip) return '127.0.0.1 (Localhost)';
    const clean = ip.trim();
    if (clean === '0:0:0:0:0:0:0:1' || clean === '::1' || clean === '127.0.0.1') {
        return '127.0.0.1 (Localhost)';
    }
    return clean;
};

const formatDateTime = (ts) => {
    if (!ts) return { date: '-', time: '' };
    try {
        const d = new Date(ts);
        return {
            date: d.toLocaleDateString(undefined, { day: '2-digit', month: '2-digit', year: 'numeric' }),
            time: d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
        };
    } catch {
        return { date: ts, time: '' };
    }
};

export default function HistoryPage() {
    const { t, i18n } = useTranslation();
    const [activeTab, setActiveTab] = useState('login'); // 'login' | 'activity'
    const [statusFilter, setStatusFilter] = useState('all');

    const [loginHistory, setLoginHistory] = useState([]);
    const [activities, setActivities] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchLoginHistory = async (status) => {
        setLoading(true);
        try {
            const data = await userService.getLoginHistory(status);
            setLoginHistory(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to fetch login history:', err);
            setLoginHistory([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchActivities = async () => {
        setLoading(true);
        try {
            const data = await userService.getSecurityActivity();
            setActivities(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error('Failed to fetch security activity:', err);
            setActivities([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (activeTab === 'login') {
            fetchLoginHistory(statusFilter);
        } else {
            fetchActivities();
        }
    }, [activeTab, statusFilter]);

    return (
        <div className="space-y-4">
            {/* Tab Selection */}
            <div className="bg-white p-1.5 rounded-2xl border border-slate-200/90 shadow-2xs flex items-center justify-between">
                <div className="flex items-center gap-1">
                    <button
                        type="button"
                        onClick={() => setActiveTab('login')}
                        className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                            activeTab === 'login'
                                ? 'bg-[#253C95] text-white shadow-sm'
                                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                        }`}
                    >
                        {i18n.language === 'en' ? 'Login History' : 'ប្រវត្តិការចូលប្រើ'}
                    </button>
                    <button
                        type="button"
                        onClick={() => setActiveTab('activity')}
                        className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                            activeTab === 'activity'
                                ? 'bg-[#253C95] text-white shadow-sm'
                                : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                        }`}
                    >
                        {i18n.language === 'en' ? 'Security Activity' : 'សកម្មភាពសុវត្ថិភាព'}
                    </button>
                </div>

                {activeTab === 'login' && (
                    <div className="flex items-center gap-2 pr-1.5">
                        <Filter size={15} className="text-slate-400 shrink-0" />
                        <CustomSelect
                            value={statusFilter}
                            onChange={(val) => setStatusFilter(val)}
                            options={[
                                { value: 'all', label: i18n.language === 'en' ? 'All Status' : 'ទាំងអស់' },
                                { value: 'success', label: i18n.language === 'en' ? 'Successful' : 'ជោគជ័យ' },
                                { value: 'failed', label: i18n.language === 'en' ? 'Failed' : 'បរាជ័យ' }
                            ]}
                            className="w-36 text-xs"
                        />
                    </div>
                )}
            </div>

            {/* TAB 1: LOGIN HISTORY TABLE */}
            {activeTab === 'login' && (
                <div className="bg-white rounded-2xl border border-slate-200/90 shadow-2xs overflow-hidden">
                    <div className="px-5 py-3.5 border-b border-slate-100 flex items-center justify-between">
                        <div>
                            <h3 className="text-sm sm:text-base font-bold text-slate-900">
                                {i18n.language === 'en' ? 'Recent Login Attempts' : 'កំណត់ត្រាចូលប្រើប្រាស់ថ្មីៗ'}
                            </h3>
                            <p className="text-xs text-slate-500 font-medium">
                                {i18n.language === 'en'
                                    ? 'Review your sign-in history to detect any unauthorized access.'
                                    : 'ពិនិត្យប្រវត្តិចូលប្រើប្រាស់ដើម្បីដឹងពីសកម្មភាពមិនប្រក្រតី។'}
                            </p>
                        </div>
                        <button
                            onClick={() => fetchLoginHistory(statusFilter)}
                            className="p-1.5 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-50 transition-all cursor-pointer"
                            title="Refresh"
                        >
                            <RefreshCw size={16} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                        </button>
                    </div>

                    <div className="overflow-x-auto">
                        {loading ? (
                            <div className="flex flex-col items-center justify-center py-10 gap-2.5 text-slate-400">
                                <Loader2 className="animate-spin text-[#253C95]" size={28} />
                                <span className="text-xs font-medium">Loading login history...</span>
                            </div>
                        ) : loginHistory.length === 0 ? (
                            <div className="text-center py-10 px-4 text-slate-400">
                                <History size={36} className="mx-auto mb-2 text-slate-300 stroke-1" />
                                <p className="text-sm font-semibold text-slate-600">
                                    {i18n.language === 'en' ? 'No login history records found' : 'មិនមានកំណត់ត្រាចូលប្រើប្រាស់ទេ'}
                                </p>
                                <p className="text-xs text-slate-400 mt-0.5">
                                    {i18n.language === 'en' ? 'New sign-in attempts will be recorded here.' : 'ការចូលប្រើថ្មីៗនឹងត្រូវកត់ត្រានៅទីនេះ។'}
                                </p>
                            </div>
                        ) : (
                            <table className="w-full text-left text-xs sm:text-sm">
                                <thead className="bg-slate-50/80 text-slate-500 text-xs uppercase font-bold tracking-wider border-b border-slate-100">
                                    <tr>
                                        <th className="px-4 py-2.5 w-28">{i18n.language === 'en' ? 'Status' : 'ស្ថានភាព'}</th>
                                        <th className="px-4 py-2.5">{i18n.language === 'en' ? 'Device & Method' : 'ឧបករណ៍ & វិធីសាស្ត្រ'}</th>
                                        <th className="px-4 py-2.5">{i18n.language === 'en' ? 'IP & Location' : 'IP & ទីតាំង'}</th>
                                        <th className="px-4 py-2.5 text-right w-40">{i18n.language === 'en' ? 'Timestamp' : 'ពេលវេលា'}</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-100 font-medium text-slate-800">
                                    {loginHistory.map((row) => {
                                        const { date, time } = formatDateTime(row.timestamp);
                                        return (
                                            <tr key={row.id} className="hover:bg-slate-50/60 transition-colors">
                                                <td className="px-4 py-2.5 whitespace-nowrap">
                                                    {row.status === 'SUCCESS' ? (
                                                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                                                            <CheckCircle2 size={13} className="text-emerald-500 shrink-0" />
                                                            {i18n.language === 'en' ? 'Success' : 'ជោគជ័យ'}
                                                        </span>
                                                    ) : (
                                                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-[#DF1F28]/10 text-[#DF1F28] border border-[#DF1F28]/20">
                                                            <XCircle size={13} className="text-[#DF1F28] shrink-0" />
                                                            {i18n.language === 'en' ? 'Failed' : 'បរាជ័យ'}
                                                        </span>
                                                    )}
                                                </td>
                                                <td className="px-4 py-2.5">
                                                    <div className="font-semibold text-slate-900 leading-tight">
                                                        {row.device || 'Web Browser'}
                                                    </div>
                                                    <div className="mt-0.5">
                                                        <span className="inline-block bg-slate-100 text-slate-600 px-2 py-0.5 rounded text-xs font-semibold uppercase tracking-wider">
                                                            {row.method || 'Password'}
                                                        </span>
                                                    </div>
                                                </td>
                                                <td className="px-4 py-2.5">
                                                    <div className="font-mono text-xs text-slate-700 font-medium leading-tight">
                                                        {formatIp(row.ipAddress)}
                                                    </div>
                                                    <div className="text-xs text-slate-400 mt-0.5 flex items-center gap-1">
                                                        <MapPin size={11} className="text-slate-400 shrink-0" />
                                                        <span className="truncate max-w-[220px]">{row.location}</span>
                                                    </div>
                                                </td>
                                                <td className="px-4 py-2.5 whitespace-nowrap text-right">
                                                    <div className="text-xs text-slate-700 font-medium">{date}</div>
                                                    <div className="text-xs text-slate-400 font-mono">{time}</div>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            )}

            {/* TAB 2: ACCOUNT ACTIVITY TIMELINE */}
            {activeTab === 'activity' && (
                <div className="bg-white p-4 sm:p-5 rounded-2xl border border-slate-200/90 shadow-2xs">
                    <div className="pb-3 border-b border-slate-100 mb-3.5 flex items-center justify-between">
                        <div>
                            <h3 className="text-sm sm:text-base font-bold text-slate-900">
                                {i18n.language === 'en' ? 'Security & Account Activity Log' : 'កំណត់ហេតុសកម្មភាពគណនី'}
                            </h3>
                            <p className="text-xs text-slate-500 font-medium">
                                {i18n.language === 'en'
                                    ? 'Audit log of significant changes made to your account.'
                                    : 'កំណត់ត្រានៃការផ្លាស់ប្តូរសំខាន់ៗដែលបានធ្វើឡើងលើគណនី។'}
                            </p>
                        </div>
                        <button
                            onClick={fetchActivities}
                            className="p-1.5 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-50 transition-all cursor-pointer"
                            title="Refresh"
                        >
                            <RefreshCw size={16} className={loading ? 'animate-spin text-[#253C95]' : ''} />
                        </button>
                    </div>

                    {loading ? (
                        <div className="flex flex-col items-center justify-center py-10 gap-2.5 text-slate-400">
                            <Loader2 className="animate-spin text-[#253C95]" size={28} />
                            <span className="text-xs font-medium">Loading security activities...</span>
                        </div>
                    ) : activities.length === 0 ? (
                        <div className="text-center py-10 px-4 text-slate-400">
                            <Activity size={36} className="mx-auto mb-2 text-slate-300 stroke-1" />
                            <p className="text-sm font-semibold text-slate-600">
                                {i18n.language === 'en' ? 'No account activities recorded yet' : 'មិនទាន់មានសកម្មភាពគណនីត្រូវបានកត់ត្រាទេ'}
                            </p>
                            <p className="text-xs text-slate-400 mt-0.5">
                                {i18n.language === 'en'
                                    ? 'Actions like password change, username update, and social account linking will appear here.'
                                    : 'សកម្មភាពដូចជាការផ្លាស់ប្តូរពាក្យសម្ងាត់ និងការភ្ជាប់គណនីនឹងបង្ហាញនៅទីនេះ។'}
                            </p>
                        </div>
                    ) : (
                        <div className="space-y-2 relative before:absolute before:inset-0 before:left-4 before:w-0.5 before:bg-slate-100">
                            {activities.map((act) => {
                                const Icon = ICON_MAP[act.icon] || Activity;
                                const { date, time } = formatDateTime(act.timestamp);
                                return (
                                    <div key={act.id} className="relative flex items-center gap-3 pl-0.5">
                                        <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 z-10 ${act.color}`}>
                                            <Icon size={15} />
                                        </div>
                                        <div className="flex-1 bg-slate-50/70 hover:bg-slate-50 px-3.5 py-2.5 rounded-xl border border-slate-100/90 flex flex-col sm:flex-row sm:items-center justify-between gap-1 transition-colors">
                                            <div>
                                                <h4 className="text-sm font-bold text-slate-900 leading-tight">
                                                    {act.event}
                                                </h4>
                                                <p className="text-xs text-slate-500 mt-0.5">
                                                    {act.description}
                                                </p>
                                            </div>
                                            <div className="text-xs text-slate-400 font-mono flex items-center gap-1.5 shrink-0 sm:text-right">
                                                <Clock size={12} className="text-slate-400 shrink-0" />
                                                <span>{date} {time}</span>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
