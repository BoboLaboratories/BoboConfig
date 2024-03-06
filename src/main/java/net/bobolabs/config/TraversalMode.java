/*
 * This file is part of BoboConfig.
 *
 * Copyright (C) 2024 BoboLabs.net
 * Copyright (C) 2024 Mattia Mignogna (https://stami.bobolabs.net)
 * Copyright (C) 2024 Fabio Nebbia (https://glowy.bobolabs.net)
 * Copyright (C) 2024 Third party contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bobolabs.config;


/**
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
 *  <caption style="display: none;">here you go</caption>
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
 * rather than primitive or list values), one could do:
 * <pre>{@code
 *  Set<String> all = section.getKeys(ALL);
 *  Set<String> leaves = section.getKeys(LEAVES);
 *  Set<String> branches = all.minus(leaves);
 * }</pre>
 * <p>
 * which would return:
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
