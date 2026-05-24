package com.aantik.dexeditorplus.dexentry;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import androidx.viewpager.widget.PagerAdapter;

import com.aantik.dexeditorplus.R;
import com.clickeffect.T_a;
import com.fastrecyclerview.FastScrollerRecyclerView;

import java.util.*;

public class TabPage extends PagerAdapter {

    private final Context ctx;
    private final String[] tabs;
    private final List<Object[]> roots = new ArrayList<Object[]>();
    private final List<Object[]> visible = new ArrayList<Object[]>();
    private RecyclerView.Adapter adapter;

    public TabPage(Context ctx, String[] tabs, List<String> classes) {
        this.ctx = ctx;
        this.tabs = tabs;
        updateClasses(classes);
    }

    public void updateClasses(List<String> classes) {
        if (classes == null) return;

        roots.clear();

        for (String cls : classes) {
            String s = (cls.startsWith("L") && cls.endsWith(";")) ? cls.substring(1, cls.length() - 1) : cls;

            String[] pts = s.split("/");
            List<Object[]> cur = roots;
            Object[] p = null;

            for (int i = 0; i < pts.length; i++) {
                boolean isC = i == pts.length - 1;
                Object[] f = null;

                for (Object[] n : cur) {
                    if (n[0].equals(pts[i]) && (Boolean) n[1] == isC) {
                        f = n;
                        break;
                    }
                }

                if (f == null) {
                    f = new Object[]{
                            pts[i],
                            Boolean.valueOf(isC),
                            Boolean.FALSE,
                            p,
                            new ArrayList<Object[]>()
                    };
                    cur.add(f);
                }

                p = f;
                cur = (List<Object[]>) f[4];
            }
        }

        process(roots);

        visible.clear();
        add(roots, visible, true);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View v, @NonNull Object o) {
        return v == o;
    }

    @Override
    public int getItemPosition(@NonNull Object o) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int p) {
        return tabs[p];
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (position != 0) {
            TextView tv = new TextView(ctx);
            tv.setText(tabs[position]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            container.addView(tv);
            return tv;
        }

        RecyclerView rv = new FastScrollerRecyclerView(ctx);
        rv.setLayoutManager(new LinearLayoutManager(ctx));

        if (rv.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);
        }

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
                View v = LayoutInflater.from(ctx).inflate(R.layout.item_class_tree, p, false);
                final RecyclerView.ViewHolder h = new RecyclerView.ViewHolder(v) {};

                T_a.apply(v, new T_a.ClickAction() {
                    @Override
                    public void onClick(@NonNull View vi) {
                        int pos = h.getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION && !(Boolean) visible.get(pos)[1]) {
                            toggle(pos, visible.get(pos), (ImageView) vi.findViewById(R.id.expand_icon));
                        }
                    }
                });

                return h;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p) {
                View v = h.itemView;
                Object[] n = visible.get(p);

                float d = ctx.getResources().getDisplayMetrics().density;

                TextView name = (TextView) v.findViewById(R.id.name_text);
                name.setText((String) n[0]);

                int dp = 0;
                for (Object[] pr = (Object[]) n[3]; pr != null; pr = (Object[]) pr[3]) {
                    dp++;
                }

                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) v.getLayoutParams();
                lp.leftMargin = (dp * (int) (25 * d)) + ((Boolean) n[1] ? (int) (17 * d) : 0);
                v.setLayoutParams(lp);

                ImageView iv = (ImageView) v.findViewById(R.id.expand_icon);
                View pkg = v.findViewById(R.id.type_icon);
                View cls = v.findViewById(R.id.class_icon);

                if ((Boolean) n[1]) {
                    iv.animate().cancel();
                    iv.setVisibility(View.GONE);
                    pkg.setVisibility(View.GONE);
                    cls.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    pkg.setVisibility(View.VISIBLE);
                    cls.setVisibility(View.GONE);
                    iv.animate().cancel();
                    iv.setRotation((Boolean) n[2] ? 40f : 0f);
                }
            }

            @Override
            public int getItemCount() {
                return visible.size();
            }
        };

        rv.setAdapter(adapter);
        container.addView(rv);
        return rv;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup c, int p, @NonNull Object o) {
        if (o instanceof RecyclerView) {
            adapter = null;
        }
        c.removeView((View) o);
    }

    private void process(List<Object[]> list) {
        Collections.sort(list, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] a, Object[] b) {
                if ((Boolean) a[1] != (Boolean) b[1]) {
                    return (Boolean) a[1] ? 1 : -1;
                }
                return ((String) a[0]).compareToIgnoreCase((String) b[0]);
            }
        });

        for (Object[] n : list) {
            if (!(Boolean) n[1]) {
                List<Object[]> ch = (List<Object[]>) n[4];

                while (ch.size() == 1 && !(Boolean) ch.get(0)[1]) {
                    Object[] c = ch.get(0);
                    n[0] = n[0] + "." + c[0];
                    n[4] = c[4];

                    ch = (List<Object[]>) n[4];

                    for (Object[] x : ch) {
                        x[3] = n;
                    }
                }

                process(ch);
            }
        }
    }

    private void add(List<Object[]> in, List<Object[]> out, boolean checkExp) {
        for (Object[] n : in) {
            out.add(n);

            if (!checkExp || (Boolean) n[2]) {
                add((List<Object[]>) n[4], out, checkExp);
            }
        }
    }

    private void toggle(int pos, Object[] n, ImageView iv) {
        n[2] = !(Boolean) n[2];

        boolean exp = (Boolean) n[2];

        iv.animate().cancel();
        iv.animate().rotation(exp ? 40f : 0f).setDuration(180).start();

        if (exp) {
            List<Object[]> ins = new ArrayList<Object[]>();
            add((List<Object[]>) n[4], ins, true);

            visible.addAll(pos + 1, ins);

            if (adapter != null) {
                adapter.notifyItemRangeInserted(pos + 1, ins.size());
            }
        } else {
            int d = 0;

            for (Object[] p = (Object[]) n[3]; p != null; p = (Object[]) p[3]) {
                d++;
            }

            int c = 0;

            for (int i = pos + 1; i < visible.size(); i++) {
                int cd = 0;

                for (Object[] cp = (Object[]) visible.get(i)[3]; cp != null; cp = (Object[]) cp[3]) {
                    cd++;
                }

                if (cd <= d) {
                    break;
                }

                c++;
            }

            visible.subList(pos + 1, pos + 1 + c).clear();

            if (adapter != null) {
                adapter.notifyItemRangeRemoved(pos + 1, c);
            }
        }
    }
}