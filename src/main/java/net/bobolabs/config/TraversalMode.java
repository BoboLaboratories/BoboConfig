/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2023 BoboLabs.net
 * Copyright (C) 2023 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2023 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2023 Third party contributors
 *
 * BoboConfig is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BoboConfig is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BoboConfig.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bobolabs.config;


/**
 * <h2>Description</h2>
 * <p>
 * An enum that specifies which paths should be returned when calling
 * {@link ConfigurationSection#getKeys(TraversalMode) getKeys} on a
 * {@link Configuration} or {@link ConfigurationSection} object.<br><br>
 * <p>
 * Assuming these are the paths relative to the {@link ConfigurationSection}
 * on which {@link ConfigurationSection#getKeys(TraversalMode) getKeys} is invoked on:
 * <ul>
 *  <li>root.section.val1</li>
 *  <li>root.section.val2</li>
 *  <li>root.val1</li>
 *  <li>root.val2</li>
 *  <li>val1</li>
 *  <li>val2</li>
 * </ul>
 * <br>
 *
 * <table>
 *  <caption></caption>
 *  <tr>
 *   <td style="vertical-align: top;">
 *    <strong>{@link #ROOT}</strong> would produce:
 *    <ul>
 *     <li>root</li>
 *     <li>val1</li>
 *     <li>val2</li>
 *    </ul>
 *   </td>
 *   <td style="vertical-align: top; padding-left: 3rem;">
 *    <strong>{@link #LEAVES}</strong> would produce:
 *    <ul>
 *     <li>root.section.val1</li>
 *     <li>root.section.val2</li>
 *     <li>root.val1</li>
 *     <li>root.val2</li>
 *     <li>val1</li>
 *     <li>val2</li>
 *    </ul>
 *   </td>
 *   <td style="vertical-align: top; padding-left: 3rem;">
 *    <strong>{@link #ALL}</strong> would produce:
 *    <ul>
 *     <li>root.section.val1</li>
 *     <li>root.section.val2</li>
 *     <li>root.section</li>
 *     <li>root.val1</li>
 *     <li>root.val2</li>
 *     <li>root</li>
 *     <li>val1</li>
 *     <li>val2</li>
 *    </ul>
 *   </td>
 *  </tr>
 * </table>
 * <br><br>
 *
 * <strong>Tip</strong>: to get branch paths only (i.e. those that are associated to {@link ConfigurationSection}s
 * rather than primitive or list values), one could do
 * <pre>{@code
 *  Set<String> all = section.getKeys(ALL);
 *  Set<String> leaves = section.getKeys(LEAVES);
 *  Set<String> branches = all.minus(leaves);
 * }</pre>
 * <p>
 * which would return
 * <ul>
 *  <li>root.section</li>
 *  <li>root</li>
 * </ul>
 *
 * @since 2.0.0
 */
public enum TraversalMode {

    /**
     * Include root level paths only.
     *
     * @since 2.0.0
     */
    ROOT,

    /**
     * Include all leaf paths only.
     *
     * @since 2.0.0
     */
    LEAVES,


    /**
     * Include all paths (i.e.&nbsp;any branch or leaf path).
     * <p>
     * Note that root paths are branch paths too.
     *
     * @since 2.0.0
     */
    ALL

}
