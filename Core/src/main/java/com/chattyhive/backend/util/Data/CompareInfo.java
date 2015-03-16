package com.chattyhive.backend.Util.Data;

/**
 * Created by Jonathan on 14/03/2015.
 */
public class CompareInfo {
            private final int COMPARE_OPTIONS_ORDINAL = 0x40000000;
            private int culture;
            private final int LINGUISTIC_IGNORECASE = 0x10;
            private final int LINGUISTIC_IGNOREDIACRITIC = 0x20;
            [NonSerialized]
            private IntPtr m_dataHandle;
            [NonSerialized]
            private IntPtr m_handleOrigin;
            [OptionalField(VersionAdded=2)]
            private String m_name;
            [NonSerialized]
            private String m_sortName;
            [OptionalField(VersionAdded=3)]
            private SortVersion m_SortVersion;
            private final int NORM_IGNORECASE = 1;
            private final int NORM_IGNOREKANATYPE = 0x10000;
            private final int NORM_IGNORENONSPACE = 2;
            private final int NORM_IGNORESYMBOLS = 4;
            private final int NORM_IGNOREWIDTH = 0x20000;
            protected final int NORM_LINGUISTIC_CASING = 0x8000000;
            private final int RESERVED_FIND_ASCII_STRING = 0x20000000;
            private final int SORT_STRINGSORT = 0x1000;
            private final int SORT_VERSION_V4 = 0x60101;
            private final int SORT_VERSION_WHIDBEY = 0x1000;
            private final CompareOptions ValidCompareMaskOffFlags = ~(CompareOptions.StringSort | CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase);
            private final CompareOptions ValidHashCodeOfStringMaskOffFlags = ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase);
            private final CompareOptions ValidIndexMaskOffFlags = ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase);
            [OptionalField(VersionAdded=1)]
            private int win32LCID;

            protected CompareInfo(CultureInfo culture)
            {
                IntPtr ptr;
                this.m_name = culture.m_name;
                this.m_sortName = culture.SortName;
                this.m_dataHandle = InternalInitSortHandle(this.m_sortName, out ptr);
                this.m_handleOrigin = ptr;
            }

            [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
            public int Compare(String string1, String string2)
            {
                return this.Compare(string1, string2, CompareOptions.None);
            }

            [SecuritySafeCritical, __DynamicallyInvokable]
            public int Compare(String string1, String string2, CompareOptions options)
            {
                if (options == CompareOptions.OrdinalIgnoreCase)
                {
                    return String.Compare(string1, string2, StringComparison.OrdinalIgnoreCase);
                }
                if ((options & CompareOptions.Ordinal) != CompareOptions.None)
                {
                    if (options != CompareOptions.Ordinal)
                    {
                        throw new ArgumentException(Environment.GetResourceString("Argument_CompareOptionOrdinal"), "options");
                    }
                    return String.CompareOrdinal(string1, string2);
                }
                if ((options & ~(CompareOptions.StringSort | CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
                {
                    throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
                }
                if (string1 == null)
                {
                    if (string2 == null)
                    {
                        return 0;
                    }
                    return -1;
                }
                if (string2 == null)
                {
                    return 1;
                }
                return InternalCompareString(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, string1, 0, string1.Length, string2, 0, string2.Length, GetNativeCompareFlags(options));
            }

            [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
            public int Compare(String string1, int offset1, String string2, int offset2)
            {
                return this.Compare(string1, offset1, string2, offset2, CompareOptions.None);
            }

            [__DynamicallyInvokable]
            public int Compare(String string1, int offset1, String string2, int offset2, CompareOptions options)
            {
                return this.Compare(string1, offset1, (string1 == null) ? 0 : (string1.Length - offset1), string2, offset2, (string2 == null) ? 0 : (string2.Length - offset2), options);
            }

            [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
            public int Compare(String string1, int offset1, int length1, String string2, int offset2, int length2)
            {
                return this.Compare(string1, offset1, length1, string2, offset2, length2, CompareOptions.None);
            }

            [SecuritySafeCritical, __DynamicallyInvokable]
            public int Compare(String string1, int offset1, int length1, String string2, int offset2, int length2, CompareOptions options)
            {
                if (options == CompareOptions.OrdinalIgnoreCase)
                {
                    int num = String.Compare(string1, offset1, string2, offset2, (length1 < length2) ? length1 : length2, StringComparison.OrdinalIgnoreCase);
                    if ((length1 == length2) || (num != 0))
                    {
                        return num;
                    }
                    if (length1 <= length2)
                    {
                        return -1;
                    }
                    return 1;
                }
                if ((length1 < 0) || (length2 < 0))
                {
                    throw new ArgumentOutOfRangeException((length1 < 0) ? "length1" : "length2", Environment.GetResourceString("ArgumentOutOfRange_NeedPosNum"));
                }
                if ((offset1 < 0) || (offset2 < 0))
                {
                    throw new ArgumentOutOfRangeException((offset1 < 0) ? "offset1" : "offset2", Environment.GetResourceString("ArgumentOutOfRange_NeedPosNum"));
                }
                if (offset1 > (((string1 == null) ? 0 : string1.Length) - length1))
                {
                    throw new ArgumentOutOfRangeException("string1", Environment.GetResourceString("ArgumentOutOfRange_OffsetLength"));
                }
                if (offset2 > (((string2 == null) ? 0 : string2.Length) - length2))
                {
                    throw new ArgumentOutOfRangeException("string2", Environment.GetResourceString("ArgumentOutOfRange_OffsetLength"));
                }
                if ((options & CompareOptions.Ordinal) != CompareOptions.None)
                {
                    if (options != CompareOptions.Ordinal)
                    {
                        throw new ArgumentException(Environment.GetResourceString("Argument_CompareOptionOrdinal"), "options");
                    }
                }
                else if ((options & ~(CompareOptions.StringSort | CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
                {
                    throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
                }
                if (string1 == null)
                {
                    if (string2 == null)
                    {
                        return 0;
                    }
                    return -1;
                }
                if (string2 == null)
                {
                    return 1;
                }
                if (options == CompareOptions.Ordinal)
                {
                    return CompareOrdinal(string1, offset1, length1, string2, offset2, length2);
                }
                return InternalCompareString(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, string1, offset1, length1, string2, offset2, length2, GetNativeCompareFlags(options));
            }

            [SecurityCritical]
            private static int CompareOrdinal(String string1, int offset1, int length1, String string2, int offset2, int length2)
            {
                int num = String.nativeCompareOrdinalEx(string1, offset1, string2, offset2, (length1 < length2) ? length1 : length2);
                if ((length1 == length2) || (num != 0))
                {
                    return num;
                }
                if (length1 <= length2)
                {
                    return -1;
                }
                return 1;
            }

            [SecuritySafeCritical]
            private SortKey CreateSortKey(String source, CompareOptions options)
            {
                if (source == null)
                {
                    throw new ArgumentNullException("source");
                }
                if ((options & ~(CompareOptions.StringSort | CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
                {
                    throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
                }
                byte[] target = null;
                if (String.IsNullOrEmpty(source))
                {
                    target = EmptyArray<byte>.Value;
                    source = "\0";
                }
                int nativeCompareFlags = GetNativeCompareFlags(options);
                int num2 = InternalGetSortKey(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, nativeCompareFlags, source, source.Length, null, 0);
                if (num2 == 0)
                {
                    throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "source");
                }
                if (target == null)
                {
                    target = new byte[num2];
                    num2 = InternalGetSortKey(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, nativeCompareFlags, source, source.Length, target, target.Length);
                }
                else
                {
                    source = String.Empty;
                }
                return new SortKey(this.Name, source, options, target);
            }

            [__DynamicallyInvokable]
            public boolean Equals(Object value)
            {
                CompareInfo info = value as CompareInfo;
                return ((info != null) && (this.Name == info.Name));
            }

        public static CompareInfo GetCompareInfo(int culture)
        {
            if (CultureData.IsCustomCultureId(culture))
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_CustomCultureCannotBePassedByNumber", new Object[] { "culture" }));
            }
            return CultureInfo.GetCultureInfo(culture).CompareInfo;
        }

        [__DynamicallyInvokable]
        public static CompareInfo GetCompareInfo(String name)
        {
            if (name == null)
            {
                throw new ArgumentNullException("name");
            }
            return CultureInfo.GetCultureInfo(name).CompareInfo;
        }

        public static CompareInfo GetCompareInfo(int culture, Assembly assembly)
        {
            if (assembly == null)
            {
                throw new ArgumentNullException("assembly");
            }
            if (assembly != typeof(Object).Module.Assembly)
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_OnlyMscorlib"));
            }
            return GetCompareInfo(culture);
        }

        public static CompareInfo GetCompareInfo(String name, Assembly assembly)
        {
            if ((name == null) || (assembly == null))
            {
                throw new ArgumentNullException((name == null) ? "name" : "assembly");
            }
            if (assembly != typeof(Object).Module.Assembly)
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_OnlyMscorlib"));
            }
            return GetCompareInfo(name);
        }

        [__DynamicallyInvokable]
        public int GetHashCode()
        {
            return this.Name.GetHashCode();
        }

        protected int GetHashCodeOfString(String source, CompareOptions options)
        {
            return this.GetHashCodeOfString(source, options, false, 0L);
        }

        [SecuritySafeCritical]
        protected int GetHashCodeOfString(String source, CompareOptions options, boolean forceRandomizedHashing, long additionalEntropy)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if ((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            if (source.Length == 0)
            {
                return 0;
            }
            return InternalGetGlobalizedHashCode(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, source, source.Length, GetNativeCompareFlags(options), forceRandomizedHashing, additionalEntropy);
        }

        protected static int GetNativeCompareFlags(CompareOptions options)
        {
            int num = 0x8000000;
            if ((options & CompareOptions.IgnoreCase) != CompareOptions.None)
            {
                num |= 1;
            }
            if ((options & CompareOptions.IgnoreKanaType) != CompareOptions.None)
            {
                num |= 0x10000;
            }
            if ((options & CompareOptions.IgnoreNonSpace) != CompareOptions.None)
            {
                num |= 2;
            }
            if ((options & CompareOptions.IgnoreSymbols) != CompareOptions.None)
            {
                num |= 4;
            }
            if ((options & CompareOptions.IgnoreWidth) != CompareOptions.None)
            {
                num |= 0x20000;
            }
            if ((options & CompareOptions.StringSort) != CompareOptions.None)
            {
                num |= 0x1000;
            }
            if (options == CompareOptions.Ordinal)
            {
                num = 0x40000000;
            }
            return num;
        }

        [TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public SortKey GetSortKey(String source)
        {
            return this.CreateSortKey(source, CompareOptions.None);
        }

        [TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public SortKey GetSortKey(String source, CompareOptions options)
        {
            return this.CreateSortKey(source, options);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, char value)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, 0, source.Length, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, String value)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, 0, source.Length, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, char value, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, 0, source.Length, options);
        }

        public int IndexOf(String source, char value, int startIndex)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, startIndex, source.Length - startIndex, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, String value, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, 0, source.Length, options);
        }

        public int IndexOf(String source, String value, int startIndex)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, startIndex, source.Length - startIndex, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, char value, int startIndex, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, startIndex, source.Length - startIndex, options);
        }

        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public int IndexOf(String source, char value, int startIndex, int count)
        {
            return this.IndexOf(source, value, startIndex, count, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int IndexOf(String source, String value, int startIndex, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.IndexOf(source, value, startIndex, source.Length - startIndex, options);
        }

        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public int IndexOf(String source, String value, int startIndex, int count)
        {
            return this.IndexOf(source, value, startIndex, count, CompareOptions.None);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public int IndexOf(String source, char value, int startIndex, int count, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if ((startIndex < 0) || (startIndex > source.Length))
            {
                throw new ArgumentOutOfRangeException("startIndex", Environment.GetResourceString("ArgumentOutOfRange_Index"));
            }
            if ((count < 0) || (startIndex > (source.Length - count)))
            {
                throw new ArgumentOutOfRangeException("count", Environment.GetResourceString("ArgumentOutOfRange_Count"));
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.IndexOf(value.ToString(), startIndex, count, StringComparison.OrdinalIgnoreCase);
            }
            if (((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None) && (options != CompareOptions.Ordinal))
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            return InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x400000) | ((source.IsAscii() && (value <= '\x007f')) ? 0x20000000 : 0), source, count, startIndex, new String(value, 1), 1);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public int IndexOf(String source, String value, int startIndex, int count, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }
            if (startIndex > source.Length)
            {
                throw new ArgumentOutOfRangeException("startIndex", Environment.GetResourceString("ArgumentOutOfRange_Index"));
            }
            if (source.Length == 0)
            {
                if (value.Length == 0)
                {
                    return 0;
                }
                return -1;
            }
            if (startIndex < 0)
            {
                throw new ArgumentOutOfRangeException("startIndex", Environment.GetResourceString("ArgumentOutOfRange_Index"));
            }
            if ((count < 0) || (startIndex > (source.Length - count)))
            {
                throw new ArgumentOutOfRangeException("count", Environment.GetResourceString("ArgumentOutOfRange_Count"));
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.IndexOf(value, startIndex, count, StringComparison.OrdinalIgnoreCase);
            }
            if (((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None) && (options != CompareOptions.Ordinal))
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            return InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x400000) | ((source.IsAscii() && value.IsAscii()) ? 0x20000000 : 0), source, count, startIndex, value, value.Length);
        }

        [SecurityCritical, SuppressUnmanagedCodeSecurity, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern int InternalCompareString(IntPtr handle, IntPtr handleOrigin, String localeName, String string1, int offset1, int length1, String string2, int offset2, int length2, int flags);
        [SecurityCritical, SuppressUnmanagedCodeSecurity, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern int InternalFindNLSStringEx(IntPtr handle, IntPtr handleOrigin, String localeName, int flags, String source, int sourceCount, int startIndex, String target, int targetCount);
        [SuppressUnmanagedCodeSecurity, SecurityCritical, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern int InternalGetGlobalizedHashCode(IntPtr handle, IntPtr handleOrigin, String localeName, String source, int length, int dwFlags, boolean forceRandomizedHashing, long additionalEntropy);
        [return: MarshalAs(UnmanagedType.Bool)]
        [SuppressUnmanagedCodeSecurity, SecurityCritical, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern boolean InternalGetNlsVersionEx(IntPtr handle, IntPtr handleOrigin, String localeName, ref Win32Native.NlsVersionInfoEx lpNlsVersionInformation);
        [SecurityCritical, SuppressUnmanagedCodeSecurity, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern int InternalGetSortKey(IntPtr handle, IntPtr handleOrigin, String localeName, int flags, String source, int sourceCount, byte[] target, int targetCount);
        [SuppressUnmanagedCodeSecurity, SecurityCritical, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern long InternalGetSortVersion();
        [SecuritySafeCritical, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        protected static IntPtr InternalInitSortHandle(String localeName, out IntPtr handleOrigin)
        {
            return NativeInternalInitSortHandle(localeName, out handleOrigin);
        }

        [return: MarshalAs(UnmanagedType.Bool)]
        [SecurityCritical, SuppressUnmanagedCodeSecurity, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern boolean InternalIsSortable(IntPtr handle, IntPtr handleOrigin, String localeName, String source, int length);
        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public boolean IsPrefix(String source, String prefix)
        {
            return this.IsPrefix(source, prefix, CompareOptions.None);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public boolean IsPrefix(String source, String prefix, CompareOptions options)
        {
            if ((source == null) || (prefix == null))
            {
                throw new ArgumentNullException((source == null) ? "source" : "prefix", Environment.GetResourceString("ArgumentNull_String"));
            }
            if (prefix.Length == 0)
            {
                return true;
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.StartsWith(prefix, StringComparison.OrdinalIgnoreCase);
            }
            if (options == CompareOptions.Ordinal)
            {
                return source.StartsWith(prefix, StringComparison.Ordinal);
            }
            if ((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            return (InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x100000) | ((source.IsAscii() && prefix.IsAscii()) ? 0x20000000 : 0), source, source.Length, 0, prefix, prefix.Length) > -1);
        }

        [ComVisible(false)]
        public static boolean IsSortable(char ch)
        {
            return IsSortable(ch.ToString());
        }

        [SecuritySafeCritical, ComVisible(false)]
        public static boolean IsSortable(String text)
        {
            if (text == null)
            {
                throw new ArgumentNullException("text");
            }
            if (text.Length == 0)
            {
                return false;
            }
            CompareInfo compareInfo = CultureInfo.InvariantCulture.CompareInfo;
            return InternalIsSortable(compareInfo.m_dataHandle, compareInfo.m_handleOrigin, compareInfo.m_sortName, text, text.Length);
        }

        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public boolean IsSuffix(String source, String suffix)
        {
            return this.IsSuffix(source, suffix, CompareOptions.None);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public boolean IsSuffix(String source, String suffix, CompareOptions options)
        {
            if ((source == null) || (suffix == null))
            {
                throw new ArgumentNullException((source == null) ? "source" : "suffix", Environment.GetResourceString("ArgumentNull_String"));
            }
            if (suffix.Length == 0)
            {
                return true;
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.EndsWith(suffix, StringComparison.OrdinalIgnoreCase);
            }
            if (options == CompareOptions.Ordinal)
            {
                return source.EndsWith(suffix, StringComparison.Ordinal);
            }
            if ((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None)
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            return (InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x200000) | ((source.IsAscii() && suffix.IsAscii()) ? 0x20000000 : 0), source, source.Length, source.Length - 1, suffix, suffix.Length) >= 0);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, char value)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.LastIndexOf(source, value, source.Length - 1, source.Length, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, String value)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.LastIndexOf(source, value, source.Length - 1, source.Length, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, char value, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.LastIndexOf(source, value, source.Length - 1, source.Length, options);
        }

        public int LastIndexOf(String source, char value, int startIndex)
        {
            return this.LastIndexOf(source, value, startIndex, startIndex + 1, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, String value, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            return this.LastIndexOf(source, value, source.Length - 1, source.Length, options);
        }

        public int LastIndexOf(String source, String value, int startIndex)
        {
            return this.LastIndexOf(source, value, startIndex, startIndex + 1, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, char value, int startIndex, CompareOptions options)
        {
            return this.LastIndexOf(source, value, startIndex, startIndex + 1, options);
        }

        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public int LastIndexOf(String source, char value, int startIndex, int count)
        {
            return this.LastIndexOf(source, value, startIndex, count, CompareOptions.None);
        }

        [__DynamicallyInvokable]
        public int LastIndexOf(String source, String value, int startIndex, CompareOptions options)
        {
            return this.LastIndexOf(source, value, startIndex, startIndex + 1, options);
        }

        [__DynamicallyInvokable, TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        public int LastIndexOf(String source, String value, int startIndex, int count)
        {
            return this.LastIndexOf(source, value, startIndex, count, CompareOptions.None);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public int LastIndexOf(String source, char value, int startIndex, int count, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if ((((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None) && (options != CompareOptions.Ordinal)) && (options != CompareOptions.OrdinalIgnoreCase))
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            if ((source.Length == 0) && ((startIndex == -1) || (startIndex == 0)))
            {
                return -1;
            }
            if ((startIndex < 0) || (startIndex > source.Length))
            {
                throw new ArgumentOutOfRangeException("startIndex", Environment.GetResourceString("ArgumentOutOfRange_Index"));
            }
            if (startIndex == source.Length)
            {
                startIndex--;
                if (count > 0)
                {
                    count--;
                }
            }
            if ((count < 0) || (((startIndex - count) + 1) < 0))
            {
                throw new ArgumentOutOfRangeException("count", Environment.GetResourceString("ArgumentOutOfRange_Count"));
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.LastIndexOf(value.ToString(), startIndex, count, StringComparison.OrdinalIgnoreCase);
            }
            return InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x800000) | ((source.IsAscii() && (value <= '\x007f')) ? 0x20000000 : 0), source, count, startIndex, new String(value, 1), 1);
        }

        [SecuritySafeCritical, __DynamicallyInvokable]
        public int LastIndexOf(String source, String value, int startIndex, int count, CompareOptions options)
        {
            if (source == null)
            {
                throw new ArgumentNullException("source");
            }
            if (value == null)
            {
                throw new ArgumentNullException("value");
            }
            if ((((options & ~(CompareOptions.IgnoreWidth | CompareOptions.IgnoreKanaType | CompareOptions.IgnoreSymbols | CompareOptions.IgnoreNonSpace | CompareOptions.IgnoreCase)) != CompareOptions.None) && (options != CompareOptions.Ordinal)) && (options != CompareOptions.OrdinalIgnoreCase))
            {
                throw new ArgumentException(Environment.GetResourceString("Argument_InvalidFlag"), "options");
            }
            if ((source.Length == 0) && ((startIndex == -1) || (startIndex == 0)))
            {
                if (value.Length != 0)
                {
                    return -1;
                }
                return 0;
            }
            if ((startIndex < 0) || (startIndex > source.Length))
            {
                throw new ArgumentOutOfRangeException("startIndex", Environment.GetResourceString("ArgumentOutOfRange_Index"));
            }
            if (startIndex == source.Length)
            {
                startIndex--;
                if (count > 0)
                {
                    count--;
                }
                if (((value.Length == 0) && (count >= 0)) && (((startIndex - count) + 1) >= 0))
                {
                    return startIndex;
                }
            }
            if ((count < 0) || (((startIndex - count) + 1) < 0))
            {
                throw new ArgumentOutOfRangeException("count", Environment.GetResourceString("ArgumentOutOfRange_Count"));
            }
            if (options == CompareOptions.OrdinalIgnoreCase)
            {
                return source.LastIndexOf(value, startIndex, count, StringComparison.OrdinalIgnoreCase);
            }
            return InternalFindNLSStringEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, (GetNativeCompareFlags(options) | 0x800000) | ((source.IsAscii() && value.IsAscii()) ? 0x20000000 : 0), source, count, startIndex, value, value.Length);
        }

        [SecurityCritical, SuppressUnmanagedCodeSecurity, DllImport("QCall", CharSet=CharSet.Unicode)]
        private static extern IntPtr NativeInternalInitSortHandle(String localeName, out IntPtr handleOrigin);
        private void OnDeserialized()
        {
            CultureInfo cultureInfo;
            IntPtr ptr;
            if (this.m_name == null)
            {
                cultureInfo = CultureInfo.GetCultureInfo(this.culture);
                this.m_name = cultureInfo.m_name;
            }
            else
            {
                cultureInfo = CultureInfo.GetCultureInfo(this.m_name);
            }
            this.m_sortName = cultureInfo.SortName;
            this.m_dataHandle = InternalInitSortHandle(this.m_sortName, out ptr);
            this.m_handleOrigin = ptr;
        }

        [OnDeserialized]
        private void OnDeserialized(StreamingContext ctx)
        {
            this.OnDeserialized();
        }

        [OnDeserializing]
        private void OnDeserializing(StreamingContext ctx)
        {
            this.m_name = null;
        }

        [OnSerializing]
        private void OnSerializing(StreamingContext ctx)
        {
            this.culture = CultureInfo.GetCultureInfo(this.Name).LCID;
        }

        [TargetedPatchingOptOut("Performance critical to inline this type of method across NGen image boundaries")]
        private void OnDeserialization(Object sender)
        {
            this.OnDeserialized();
        }

        [__DynamicallyInvokable]
        public String ToString()
        {
            return ("CompareInfo - " + this.Name);
        }

        private static long InternalSortVersion
        {
            [SecuritySafeCritical]
            get
            {
                return InternalGetSortVersion();
            }
        }

        protected static boolean IsLegacy20SortingBehaviorRequested
        {
            get
            {
                return (InternalSortVersion == 0x1000);
            }
        }

        public int LCID
        {
            get
            {
                return CultureInfo.GetCultureInfo(this.Name).LCID;
            }
        }

        [ComVisible(false), __DynamicallyInvokable]
        public String Name
        {
            [__DynamicallyInvokable]
            get
            {
                if (!(this.m_name == "zh-CHT") && !(this.m_name == "zh-CHS"))
                {
                    return this.m_sortName;
                }
                return this.m_name;
            }
        }

        public SortVersion Version
        {
            [SecuritySafeCritical]
            get
            {
                if (this.m_SortVersion == null)
                {
                    Win32Native.NlsVersionInfoEx lpNlsVersionInformation = new Win32Native.NlsVersionInfoEx {
                    dwNLSVersionInfoSize = Marshal.SizeOf(typeof(Win32Native.NlsVersionInfoEx))
                };
                    InternalGetNlsVersionEx(this.m_dataHandle, this.m_handleOrigin, this.m_sortName, ref lpNlsVersionInformation);
                    this.m_SortVersion = new SortVersion(lpNlsVersionInformation.dwNLSVersion, (lpNlsVersionInformation.dwEffectiveId != 0) ? lpNlsVersionInformation.dwEffectiveId : this.LCID, lpNlsVersionInformation.guidCustomVersion);
                }
                return this.m_SortVersion;
            }
        }
        }

